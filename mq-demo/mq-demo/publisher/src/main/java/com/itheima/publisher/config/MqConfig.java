package com.itheima.publisher.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MqConfig {

    private final RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init(){
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("监听到了消息return callback：{}");
            log.debug("exchage：{}", returned.getExchange());
            log.debug("routingKey：{}", returned.getRoutingKey());
            log.debug("replyCode：{}", returned.getReplyCode());
            log.debug("replyText：{}", returned.getReplyText());
            log.debug("message：{}", returned.getMessage());
        });
    }
}
