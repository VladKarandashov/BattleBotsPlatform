package ru.abradox.battlegateway.service;

import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.UUID;

public interface BattleConnectionsService {

    void handleUserMessage(UUID botToken, String userMessage);

    void sendMessageToUser(UUID botToken, String myMessage);

    void putConnection(UUID botToken, WebSocketSession connection);

    void closeConnection(UUID botToken);

    void clearConnection(UUID botToken);

    void clearConnections();

    void spamMessages();
}
