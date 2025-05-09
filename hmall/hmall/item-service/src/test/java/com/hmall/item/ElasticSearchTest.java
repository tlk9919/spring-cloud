package com.hmall.item;

import cn.hutool.json.JSONUtil;
import com.hmall.item.domain.po.ItemDoc;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/*@SpringBootTest(properties ="spring.profiles.active=local" )*/
public class ElasticSearchTest {
    private RestHighLevelClient client;

    @BeforeEach
    void setUp() {
        client = new RestHighLevelClient(RestClient.
                builder(HttpHost.create("http://192.168.67.126:9200")
                ));
    }

    @AfterEach
    void tearDown() throws IOException {
        if (client!=null) {
            client.close();
        }
    }

    @Test
    void testMatchAll() throws IOException {
        //1.创建request对象
        SearchRequest request = new SearchRequest("items");
        //2.配置request参数
        request.source()
                .query(QueryBuilders.matchAllQuery());
        //3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        parseResponseResult(response);

    }

    @Test
    void testSearch() throws IOException {
        //1.创建request对象
        SearchRequest request = new SearchRequest("items");
        //2.组织DSL参数
        request.source().query(
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("name", "脱脂牛奶"))
                        .filter(QueryBuilders.termQuery("brand", "德亚"))
                        .filter(QueryBuilders.rangeQuery("price").lt(300000))
        )
        ;
        //3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        parseResponseResult(response);

    }
    @Test
    void testSortAndPage() throws IOException {
        //0.0模拟分页的参数
        int pageNo = 9, pageSize = 5;
        //1.创建request对象
        SearchRequest request = new SearchRequest("items");
        //2.组织DSL参数
        //2.1query条件
        request.source().query(QueryBuilders.matchAllQuery());
        //2.2分页
        request.source().from((pageNo - 1)*pageSize).size(pageSize);
        request.source()
                .sort("price", SortOrder.DESC)
                .sort("sold", SortOrder.ASC);
        //3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        parseResponseResult(response);

    }
    @Test
    void testHighlight() throws IOException {
        //1.创建request对象
        SearchRequest request = new SearchRequest("items");
        //2.组织DSL参数
        //2.1query条件
        request.source().query(QueryBuilders.matchQuery("name", "脱脂牛奶"));
        //2.2尚亮条件
        request.source().highlighter(SearchSourceBuilder.highlight().field("name"));
        //3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        parseResponseResult(response);

    }
    @Test
    void testAgg() throws IOException {
        //1.创建request对象
        SearchRequest request = new SearchRequest("items");
        //2.组织DSL参数
        //2.1分页条件
        request.source().size();
        //2.2聚合条件（三要素：类型、名称、字段）
        String brandAggName = "brandAgg";
        request.source().aggregation(
                AggregationBuilders
                        .terms(brandAggName)
                        .field("brand.keyword")
                        .size(10)
        );
        //3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

       //4.解析结果
        Aggregations aggregations = response.getAggregations();
        // 4.1根据名称获取聚合结果
        Terms brandTerms = aggregations.get(brandAggName);
        //4.2获取buckets
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        //4.3遍历获取buckets
        for (Terms.Bucket bucket : buckets) {
            System.out.println(bucket.getKeyAsString());
            System.out.println(bucket.getDocCount());
        }


    }

    private static void parseResponseResult(SearchResponse response) {
        //4.处理结果
        SearchHits searchHits = response.getHits();
        //4.1总条数
        long total = searchHits.getTotalHits().value;
        System.out.println("total = " + total);
        //4.2命中的数据
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            //4.2.1获取source结果
            String json = hit.getSourceAsString();
            //4.2.2转成ItemDoc
            ItemDoc itemDoc = JSONUtil.toBean(json, ItemDoc.class);
            //4.3处理高亮结果
            Map<String, HighlightField> hfs = hit.getHighlightFields();
            if (hfs!=null && hfs.size()>0) {
                //4.3.1获取高亮结果
                HighlightField hf = hfs.get("name");
                String hfName = hf.getFragments()[0].toString();
                //4.3.2覆盖非高亮结果
                itemDoc.setName(hfName);
            }
            System.out.println("itemDoc = " + itemDoc);
        }
    }

}
