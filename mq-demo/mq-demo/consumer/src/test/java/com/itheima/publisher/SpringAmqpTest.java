package com.itheima.publisher;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}
