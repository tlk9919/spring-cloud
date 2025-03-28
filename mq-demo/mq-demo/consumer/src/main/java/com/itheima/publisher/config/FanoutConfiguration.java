package com.itheima.publisher.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FanoutConfiguration {

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("hmall.fanout");
    }
    @Bean
    public Queue fanoutQueue1() {
        return new Queue("fanout.queue1", true);
    }
    @Bean
    public Binding fanoutQueue1Binding(FanoutExchange fanoutExchange, Queue fanoutQueue1) {
        return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
    }
    @Bean
    public Queue fanoutQueue2() {
        return new Queue("fanout.queue2", true);
    }
    @Bean
    public Binding fanoutQueue2Binding(FanoutExchange fanoutExchange, Queue fanoutQueue2) {
        return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
    }
}
