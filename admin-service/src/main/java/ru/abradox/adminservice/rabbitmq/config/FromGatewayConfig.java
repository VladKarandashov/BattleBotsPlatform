package ru.abradox.adminservice.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FromGatewayConfig {

    @Bean
    public Queue playerActionsQueue() {
        return QueueBuilder.durable("player-actions")
                .ttl(5*1000) // время жизни сообщения
                .build();
    }

    @Bean
    public FanoutExchange playerActionsExchange() {
        return new FanoutExchange("player-actions");
    }

    @Bean
    public Binding playerActionsBinding() {
        return BindingBuilder.bind(playerActionsQueue()).to(playerActionsExchange());
    }
}
