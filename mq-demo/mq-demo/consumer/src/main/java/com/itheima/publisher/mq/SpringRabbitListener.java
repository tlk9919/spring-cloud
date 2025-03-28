package com.itheima.publisher.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class SpringRabbitListener {


    @RabbitListener(queues = "simple.queue")
    public void ListenSimpleQueue(String message) {
        log.info("SpringRabbitListener收到消息：【{}】", message);
    }
    @RabbitListener(queues = "work.queue")
    public void ListenWorkQueue1(String message) throws InterruptedException {

        System.out.println("消费者1接收到消息"+message+ ","+LocalDateTime.now());
    }
    @RabbitListener(queues = "work.queue")
    public void ListenWorkQueue2(String message) throws InterruptedException {
        System.out.println("消费者2接收到消息"+message+","+ LocalDateTime.now());
    }
    @RabbitListener(queues = "fanout.queue1")
    public void ListenFanoutQueue1(String message) throws InterruptedException {
        log.info("SpringRabbitListener收到消息：【{}】", message);
    }
    @RabbitListener(queues = "fanout.queue2")
    public void ListenFanoutQueue2(String message) throws InterruptedException {
        log.info("SpringRabbitListener收到消息：【{}】", message);
    }
    @RabbitListener(queues = "direct.queue1")
    public void ListenDirectQueue1(String message) throws InterruptedException {
        log.info("SpringRabbitListener收到消息：【{}】", message);
    }
    @RabbitListener(queues = "direct.queue2")
    public void ListenDirectQueue2(String message) throws InterruptedException {
        log.info("SpringRabbitListener收到消息：【{}】", message);
    }
    @RabbitListener(queues = "topic.queue1")
    public void ListenTopicQueue1(String message) throws InterruptedException {
        log.info("SpringRabbitListener-1收到消息：【{}】", message);
    }
    @RabbitListener(queues = "topic.queue2")
    public void ListenTopicQueue2(String message) throws InterruptedException {
        log.info("SpringRabbitListener-2收到消息：【{}】", message);
    }
}
