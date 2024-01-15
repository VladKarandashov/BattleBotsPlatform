package ru.abradox.battlegateway.service;

import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ConnectionService {

    void handleUserMessage(UUID botToken, String userMessage);

    void sendMessageToUser(UUID botToken, String myMessage);

    void putConnection(UUID botToken, WebSocketSession connection);

    void closeConnection(UUID botToken);

    void clearConnection(UUID botToken);

    Optional<WebSocketSession> getConnection(UUID botToken);

    Map<UUID, WebSocketSession> getConnections();
}
