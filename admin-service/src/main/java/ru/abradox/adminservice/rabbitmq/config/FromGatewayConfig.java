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

    @Bean
    public Queue activeConnectionsQueue() {
        return QueueBuilder.durable("active-connections")
                .ttl(2*1000) // время жизни сообщения
                .build();
    }

    @Bean
    public FanoutExchange activeConnectionsExchange() {
        return new FanoutExchange("active-connections");
    }

    @Bean
    public Binding activeConnectionsBinding() {
        return BindingBuilder.bind(playerActionsQueue()).to(playerActionsExchange());
    }
}
