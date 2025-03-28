package com.itheima.publisher;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class SpringAmqpTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void testSimpleQueue() {
        //1.队列名
        String queueName = "simple.queue";
        //2.消息
        String message = "hello,spring amqp!";
        //3.发送消息
        rabbitTemplate.convertAndSend(queueName, message);
    }

    @Test
    void testWorkQueue() {
        //1.队列名
        String queueName = "work.queue";

        for (int i = 1; i <=50 ; i++) {
            //2.消息
            String message = "hello,spring amqp-work!"+i;
            //3.发送消息
            rabbitTemplate.convertAndSend(queueName, message);
        }

    }
    @Test
    void testFanoutQueue() {
        //1.交换机名
        String exchangeName = "hmall.fanout";

        for (int i = 1; i <=50 ; i++) {
            //2.消息
            String message = "hello,spring everyone!"+i;
            //3.发送消息
            rabbitTemplate.convertAndSend(exchangeName, null, message);
        }

    }
    @Test
    void testDirectQueue() {
        //1.交换机名
        String exchangeName = "hmall.direct";

            //2.消息
            String message = "红色，，，";
            //3.发送消息
            rabbitTemplate.convertAndSend(exchangeName, "yellow", message);

    }
    @Test
     void testTopicQueue() {
        //1.交换机名
        String exchangeName = "hmall.topic";

        //2.消息
        String message = "天气真好";
        //3.发送消息
        rabbitTemplate.convertAndSend(exchangeName, "china.weather", message);

    }
    @Test
    void testSendObject() {
        Map<String, Object> msg=new HashMap<>(2);
        msg.put("name","Jack");
        msg.put("age",18);
        rabbitTemplate.convertAndSend("object.queue", msg);
    }
}
