package com.itheima.publisher.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ErrorMessageConfiguration {


    @Bean
    public DirectExchange errorMessageExchange() {
        return new DirectExchange("error.direct");
    }
    @Bean
    public Queue errorMessageQueue() {
        return new Queue("error.queue");
    }
    @Bean
    public Binding errorMessageBinding(DirectExchange errorMessageExchange,Queue errorMessageQueue) {
        return BindingBuilder.bind(errorMessageQueue).to(errorMessageExchange).with("error");
    }
    //失败处理策略
    @Bean
    public MessageRecoverer messageRecoverer( RabbitTemplate rabbitTemplate)  {
        return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error");
    }
}
