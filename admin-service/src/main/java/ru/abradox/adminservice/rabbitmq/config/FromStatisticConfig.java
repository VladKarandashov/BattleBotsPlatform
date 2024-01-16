package ru.abradox.adminservice.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FromStatisticConfig {

    @Bean
    public Queue startRoundQueue() {
        return QueueBuilder.durable("start-round")
                .ttl(10*1000) // время жизни сообщения
                .build();
    }

    @Bean
    public FanoutExchange startRoundExchange() {
        return new FanoutExchange("start-round");
    }

    @Bean
    public Binding startRoundBinding() {
        return BindingBuilder.bind(startRoundQueue()).to(startRoundExchange());
    }
}
