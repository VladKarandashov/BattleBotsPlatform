package ru.abradox.battlegateway.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import ru.abradox.battlegateway.client.token.TokenHolder;
import ru.abradox.battlegateway.service.BattleConnectionsService;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class BattleConnectionsServiceImpl implements BattleConnectionsService {

    private final ConcurrentHashMap<UUID, WebSocketSession> connections = new ConcurrentHashMap<>();

    private final RabbitTemplate rabbitTemplate;
    private final TokenHolder tokenHolder;

    @Override
    public void handleUserMessage(UUID botToken, String userMessage) {
        log.info("От пользователя {} получено сообщение {}", botToken, userMessage);
        rabbitTemplate.convertAndSend("player-actions", "", userMessage);
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
        if (connections.containsKey(botToken)) {
            log.info("Закрываю соединение с {}", botToken);
            var session = connections.get(botToken);
            session.close().subscribe();
            connections.remove(botToken);
        }
    }

    @Override
    @Async
    @Scheduled(fixedRate = 5 * 1000)
    public void clearConnections() {
        connections.forEach((uuid, session) -> {
            if (tokenHolder.isNotTokenExist(uuid)) {
                closeConnection(uuid);
            }
        });
        log.info("Подключено {} ботов", connections.size());
    }

    @Override
    @Async
    @Scheduled(fixedRate = 5 * 1000)
    // FIXME удалить
    public void spamMessages() {
        getRandomUserId().ifPresentOrElse(
                botToken -> sendMessageToUser(botToken, "test message"),
                () -> log.info("Нет подключенного пользователя"));
    }

    // FIXME удалить
    private Optional<UUID> getRandomUserId() {
        if (connections.isEmpty()) return Optional.empty();
        return Optional.of(connections.keys().nextElement());
    }
}
