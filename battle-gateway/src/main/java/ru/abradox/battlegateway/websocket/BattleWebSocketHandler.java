package ru.abradox.battlegateway.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import ru.abradox.battlegateway.service.AuthService;
import ru.abradox.battlegateway.service.BattleConnectionsService;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class BattleWebSocketHandler implements WebSocketHandler {

    private final BattleConnectionsService connectionsService;
    private final AuthService authService;

    public BattleWebSocketHandler(BattleConnectionsService connectionsService, AuthService authService) {
        this.connectionsService = connectionsService;
        this.authService = authService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // Получение параметров для проверки пользователя
        var headers = session.getHandshakeInfo().getHeaders();
        var botNameHeader = headers.getFirst("botName");
        var botTokenHeader = headers.getFirst("botToken");
        if (!authService.checkBotAccess(botNameHeader, botTokenHeader)) {
            log.info("Бот [{} {}] не прошёл аутентификацию", botNameHeader, botTokenHeader);
            return session.close(); // Закрываем соединение, если проверка не прошла
        }
        var botToken = UUID.fromString(Objects.requireNonNull(botTokenHeader));

        connectionsService.putConnection(botToken, session);

        return session.receive() // Получаем сообщения от пользователя
                .doOnNext(message -> {
                    String userMessage = message.getPayloadAsText();
                    connectionsService.handleUserMessage(botToken, userMessage);
                })
                .doOnTerminate(() -> {
                    log.info("Произошёл terminate");
                    connectionsService.closeConnection(botToken);
                })
                .then();
    }
}