package ru.abradox.battlegateway.service;

import org.springframework.web.reactive.socket.WebSocketSession;
import ru.abradox.platformapi.cardgame.event.ServerResponse;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ConnectionService {

    void handleUserMessage(UUID botToken, String userMessage);

    void sendMessageToBot(UUID botToken, ServerResponse response);

    void putConnection(UUID botToken, WebSocketSession connection);

    void closeConnection(UUID botToken);

    void clearConnection(UUID botToken);

    Optional<WebSocketSession> getConnection(UUID botToken);

    Map<UUID, WebSocketSession> getConnections();
}
