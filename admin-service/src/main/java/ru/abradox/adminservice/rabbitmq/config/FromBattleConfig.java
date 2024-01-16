package ru.abradox.adminservice.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FromBattleConfig {

    @Bean
    public Queue finishRoundQueue() {
        return QueueBuilder.durable("finish-round")
                .build();
    }

    @Bean
    public FanoutExchange finishRoundExchange() {
        return new FanoutExchange("finish-round");
    }

    @Bean
    public Binding finishRoundBinding() {
        return BindingBuilder.bind(finishRoundQueue()).to(finishRoundExchange());
    }
}
