package com.itheima.publisher.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NormalConfiguration {

    @Bean
    public DirectExchange normalExchange() {
        return new DirectExchange("normal.direct");
    }

 /*   @Bean
    public DirectExchange normalExchange() {
        return  ExchangeBuilder.directExchange("normal.fanout").durable(true).build();
    }*/
    @Bean
    public Queue normalQueue() {
        return QueueBuilder
                .durable("normal.queue")
                .deadLetterExchange("dlx.direct")
                .build();
    }

    @Bean
    public Binding normalQueueBinding( DirectExchange normalExchange, Queue normalQueue) {
        return BindingBuilder.bind(normalQueue).to(normalExchange).with("hi");
    }
}
