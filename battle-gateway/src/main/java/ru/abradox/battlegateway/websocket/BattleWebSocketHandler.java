package ru.abradox.battlegateway.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import ru.abradox.battlegateway.service.AuthService;
import ru.abradox.battlegateway.service.ConnectionService;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class BattleWebSocketHandler implements WebSocketHandler {

    private final ConnectionService connectionService;
    private final AuthService authService;

    public BattleWebSocketHandler(ConnectionService connectionService, AuthService authService) {
        this.connectionService = connectionService;
        this.authService = authService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // Получение параметров для проверки пользователя
        var headers = session.getHandshakeInfo().getHeaders();
        var header = headers.getFirst("Sec-WebSocket-Protocol");
        log.info("Пришёл бот с параметрами: '{}'", header);
        var parts = Optional.ofNullable(header)
                .map(s -> s.split(", "))
                .map(s -> (s.length == 2) ? s : null)
                .orElse(new String[]{"", ""});
        log.info("После разделения получил параметры '{}'", Arrays.toString(parts));
        var botNameHeader = parts[0].strip();
        var botTokenHeader = parts[1].strip();

        log.info("Начинаю аутентификацию для '{}' '{}'", botNameHeader, botTokenHeader);
        if (!authService.checkBotAccess(botNameHeader, botTokenHeader)) {
            log.error("Бот ['{}' '{}'] не прошёл аутентификацию", botNameHeader, botTokenHeader);
            return session.close(); // Закрываем соединение, если проверка не прошла
        }
        log.info("Аутентификация для '{}' '{}' прошла успешно", botNameHeader, botTokenHeader);
        var botToken = UUID.fromString(Objects.requireNonNull(botTokenHeader));

        connectionService.putConnection(botToken, session);

        log.info("Соединение '{}' '{}' установлено", botNameHeader, botTokenHeader);
        return session.receive() // Получаем сообщения от пользователя
                .doOnNext(message -> {
                    String userMessage = message.getPayloadAsText();
                    connectionService.handleUserMessage(botToken, userMessage);
                })
                .doOnTerminate(() -> connectionService.clearConnection(botToken))
                .then();
    }
}