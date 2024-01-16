package ru.abradox.battlegateway.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import ru.abradox.battlegateway.service.ConnectionService;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    @Getter
    private final Map<UUID, WebSocketSession> connections = new ConcurrentHashMap<>();

    private final RabbitTemplate rabbitTemplate;

    @Override
    // TODO прикрутить res4j: rateLimiter и сообщение NOT SPAM
    public void handleUserMessage(UUID botToken, String userMessage) {
        log.info("От пользователя {} получено сообщение {}", botToken, userMessage);
        rabbitTemplate.convertAndSend("bot-actions", "", userMessage);
    }

    @Override
    public void sendMessageToUser(UUID botToken, String myMessage) {
        WebSocketSession session = connections.get(botToken);
        if (session != null && session.isOpen()) {
            session.send(Mono.just(session.textMessage(myMessage))).subscribe(); // Отправляем сообщение
            return;
        }
        log.info("Сообщение для пользователя {} не может быть доставлено", botToken);
        if (connections.containsKey(botToken)) closeConnection(botToken);
    }

    @Override
    public void putConnection(UUID botToken, WebSocketSession connection) {
        closeConnection(botToken);
        connections.put(botToken, connection);
    }

    @Override
    public void closeConnection(UUID botToken) {
        getConnection(botToken)
                .ifPresent(session -> session.close().subscribe());
    }

    @Override
    public void clearConnection(UUID botToken) {
        getConnection(botToken)
                .filter(session -> !session.isOpen())
                .ifPresent(session -> connections.remove(botToken));
    }

    public Optional<WebSocketSession> getConnection(UUID botToken) {
        return Optional.ofNullable(connections.get(botToken));
    }
}
