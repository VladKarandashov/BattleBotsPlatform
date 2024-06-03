package ru.abradox.adminservice.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FromGatewayConfig {

    @Bean
    public Queue botActionsQueue() {
        return QueueBuilder.durable("bot-actions")
                .ttl(5*1000) // время жизни сообщения
                .build();
    }

    @Bean
    public FanoutExchange botActionsExchange() {
        return new FanoutExchange("bot-actions");
    }

    @Bean
    public Binding botActionsBinding() {
        return BindingBuilder.bind(botActionsQueue()).to(botActionsExchange());
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
        return BindingBuilder.bind(activeConnectionsQueue()).to(activeConnectionsExchange());
    }
}
