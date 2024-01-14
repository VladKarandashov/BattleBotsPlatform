package ru.abradox.battlegateway.websocket;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import ru.abradox.battlegateway.service.MessageService;
import ru.abradox.battlegateway.service.impl.TokenHolder;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BattleWebSocketHandler implements WebSocketHandler {

    private final MessageService messageService;
    private final TokenHolder tokenHolder;

    private final ConcurrentHashMap<UUID, WebSocketSession> connections = new ConcurrentHashMap<>();

    public BattleWebSocketHandler(MessageService messageService, TokenHolder tokenHolder) {
        this.messageService = messageService;
        this.tokenHolder = tokenHolder;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // Получение параметров для проверки пользователя
        var headers = session.getHandshakeInfo().getHeaders();
        var botNameHeader = headers.getFirst("botName");
        var botTokenHeader = headers.getFirst("botToken");
        if (!messageService.checkUser(botNameHeader, botTokenHeader)) {
            return session.close(); // Закрываем соединение, если проверка не прошла
        }
        var botToken = UUID.fromString(Objects.requireNonNull(botTokenHeader));

        connections.put(botToken, session);

        return session.receive() // Получаем сообщения от пользователя
                .doOnNext(message -> {
                    String userMessage = message.getPayloadAsText();
                    messageService.handleUserMessage(botToken, userMessage);
                })
                .doOnTerminate(() -> connections.remove(botToken)) // Удаляем соединение при его закрытии
                .then();
    }

    public void sendMessageToUser(UUID botToken, String myMessage) {
        WebSocketSession session = connections.get(botToken);
        if (session != null) {

            session.send(Mono.just(session.textMessage(myMessage))).subscribe(); // Отправляем сообщение
        }
        // Обработка случая, когда сессии нет, не представлена
    }

    @Async
    @Scheduled(fixedRate = 5 * 1000)
    protected void clearConnections() {
        connections.forEach((uuid, session) -> {
            if (!tokenHolder.isTokenExist(uuid)) { // Проверяем, существует ли токен
                session.close().subscribe(); // Закрываем WebSocketSession
                connections.remove(uuid); // Удаляем соединение из connections
            }
        });
    }

    // FIXME удалить
    public Optional<UUID> getRandomUserId() {
        if (connections.isEmpty()) return Optional.empty();
        return Optional.of(connections.keys().nextElement());
    }
}