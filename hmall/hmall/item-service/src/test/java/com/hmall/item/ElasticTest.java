package com.hmall.item;

import org.apache.http.HttpHost;
import org.aspectj.lang.annotation.Before;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ElasticTest {
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
    void testConnection() {
        System.out.println("连接成功client"+client);
    }

    @AfterAll
    static void afterAll() {

    }
}
