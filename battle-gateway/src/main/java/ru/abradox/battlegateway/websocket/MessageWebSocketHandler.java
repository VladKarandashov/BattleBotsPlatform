package ru.abradox.battlegateway.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import ru.abradox.battlegateway.service.MessageService;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageWebSocketHandler implements WebSocketHandler {

    private final MessageService messageService;
    private final ConcurrentHashMap<UUID, WebSocketSession> connections = new ConcurrentHashMap<>();

    public MessageWebSocketHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // Получение параметров для проверки пользователя
        var headers = session.getHandshakeInfo().getHeaders();
        String userName = headers.getFirst("userName");
        UUID userId = UUID.fromString(Objects.requireNonNull(headers.getFirst("userId")));
        if (!messageService.checkUser(userName, userId)) {
            return session.close(); // Закрываем соединение, если проверка не прошла
        }

        connections.put(userId, session);

        return session.receive() // Получаем сообщения от пользователя
                .doOnNext(message -> {
                    String userMessage = message.getPayloadAsText();
                    messageService.handleUserMessage(userId, userMessage);
                })
                .doOnTerminate(() -> connections.remove(userId)) // Удаляем соединение при его закрытии
                .then();
    }

    public void sendMessageToUser(UUID userId, String myMessage) {
        WebSocketSession session = connections.get(userId);
        if (session != null) {

            session.send(Mono.just(session.textMessage(myMessage))).subscribe(); // Отправляем сообщение
        }
        // Обработка случая, когда сессии нет, не представлена
    }

    public Optional<UUID> getRandomUserId() {
        if (connections.isEmpty()) return Optional.empty();
        return Optional.of(connections.keys().nextElement());
    }
}