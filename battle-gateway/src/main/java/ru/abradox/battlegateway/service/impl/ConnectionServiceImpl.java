package ru.abradox.battlegateway.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import ru.abradox.battlegateway.service.ConnectionService;
import ru.abradox.platformapi.cardgame.event.ServerResponse;

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
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RabbitTemplate rabbitTemplate;

    @Override
    // TODO прикрутить res4j: rateLimiter и сообщение NOT SPAM
    public void handleUserMessage(UUID botToken, String userMessage) {
        log.info("От пользователя {} получено сообщение {}", botToken, userMessage);
        rabbitTemplate.convertAndSend("bot-actions", "", userMessage);
    }

    @Override
    public void sendMessageToBot(UUID botToken, ServerResponse serverResponse) {
        WebSocketSession session = connections.get(botToken);
        if (session != null && session.isOpen()) {
            var response = writeServerResponseAsJson(serverResponse);
            session.send(Mono.just(session.textMessage(response))).subscribe(); // Отправляем сообщение
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

    @Override
    public Optional<WebSocketSession> getConnection(UUID botToken) {
        return Optional.ofNullable(connections.get(botToken));
    }

    @SneakyThrows
    private String writeServerResponseAsJson(ServerResponse response) {
        return objectMapper.writeValueAsString(response);
    }
}
