package com.itheima.publisher;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@Slf4j
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
    @Test
    void testConfirmCallback() throws InterruptedException {
        //0.创建correlationData
        CorrelationData cd = new CorrelationData(UUID.randomUUID().toString());
        cd.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
            @Override
            public void onFailure(Throwable ex) {
                //是内部逻辑错误才执行，一般不会执行
                log.error("spring-amqp处理确认结果失败", ex);
            }

            @Override
            public void onSuccess(CorrelationData.Confirm result) {
                //判断是否成功
                if (result.isAck()) {
                    log.debug("收到confirmCallback ack，消息发送成功");
                } else {
                    log.error("收到confirmCallback nack，消息发送失败",result.getReason());
                }
                }
        });
        //1.交换机名
        String exchangeName = "hmall.direct";

        //2.消息
        String message = "红色，，，";
        //3.发送消息
        rabbitTemplate.convertAndSend(exchangeName, "yellow1", message,cd);
        Thread.sleep(2000);

    }
   // @Test
    void testSendMessage(){
        //1.自定义构建消息
        Message msg = MessageBuilder.withBody("hello,SpringAMQP".getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT  ).build();
        //2.发送消息
        for (int i = 0; i <1000000 ; i++) {

            rabbitTemplate.convertAndSend("lazy.queue",msg);
        }
    }
}
