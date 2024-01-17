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

    @Bean
    public Queue botResponseQueue() {
        return QueueBuilder.durable("bot-response")
                .build();
    }

    @Bean
    public FanoutExchange botResponseExchange() {
        return new FanoutExchange("bot-response");
    }

    @Bean
    public Binding botResponseBinding() {
        return BindingBuilder.bind(botResponseQueue()).to(botResponseExchange());
    }
}
