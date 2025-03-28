package com.itheima.publisher.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpringRabbitListener {


    @RabbitListener(queues = "simple.queue")
    public void ListenSimpleQueue(String message) {
        log.info("SpringRabbitListener收到消息：{}", message);
    }
}
