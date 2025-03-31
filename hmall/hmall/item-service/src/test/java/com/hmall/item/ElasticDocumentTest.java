package com.hmall.item;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.item.domain.po.Item;
import com.hmall.item.domain.po.ItemDoc;
import com.hmall.item.service.IItemService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest(properties ="spring.profiles.active=local" )
public class ElasticDocumentTest {
    private RestHighLevelClient client;
    @Autowired
    private IItemService  iItemService;

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

    //又是修改也是新增
    @Test
    void testIndexDoc() throws IOException {
        //0.准备文档数据
        //0.1根据id查询数据库数据
        Item item = iItemService.getById(317578L);
        //0.2将数据库数据转换为文档对象
        ItemDoc itemDoc = BeanUtil.copyProperties(item, ItemDoc.class);

        //1.准备Request对象
        IndexRequest request = new IndexRequest("items").id(item.getId().toString());
        //2.准备请求参数
        request.source(JSONUtil.toJsonStr(itemDoc),XContentType.JSON);
        //3.发送请求
        client.index(request, RequestOptions.DEFAULT);
    }

    @Test
    void testGetDoc() throws IOException {

        //1.准备Request对象
        GetRequest request = new GetRequest("items","317578");
        //2.发送请求
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        //3.解析结果source结果
        String json = response.getSourceAsString();
        System.out.println("json:"+json);
        ItemDoc doc = JSONUtil.toBean(json, ItemDoc.class);
        System.out.println("doc:"+doc);
    }
    @Test
    void testDeleteDoc() throws IOException {

        //1.准备Request对象
        DeleteRequest request = new DeleteRequest("items","317578");
        //2.发送请求
        client.delete(request, RequestOptions.DEFAULT);
    }

    @Test
    void testUpdateDoc() throws IOException {

        //1.准备Request对象
        UpdateRequest request = new UpdateRequest("items","317578");
        //1.1准备请求参数
        request.doc(
                "price", 100000
        );
        //2.发送请求
        client.update(request, RequestOptions.DEFAULT);
    }
    @Test
    void testBulkDoc() throws IOException {
        int pageNo = 1 ,pageSize = 500;
        while (true){
            Page<Item> itemPage = new Page<>(pageNo, pageSize);

            //0.0准备批量数据
            Page<Item> page = iItemService.lambdaQuery()
                    .eq(Item::getStatus, 1)
                    .page(itemPage);

            List<Item> records = page.getRecords();

            if (records == null || records.isEmpty()) {
                return;
            }
            //1.准备Request对象
            BulkRequest request = new BulkRequest();
            //2.准备请求参数
            for (Item item : records) {
                request.add(new IndexRequest("items")
                        .id(item.getId().toString())
                        .source(JSONUtil.toJsonStr(BeanUtil.copyProperties(item, ItemDoc.class)),XContentType.JSON));
            }
            //3.发送请求
            client.bulk(request, RequestOptions.DEFAULT);
            //4.翻页
            pageNo++;
        }
    }
}
