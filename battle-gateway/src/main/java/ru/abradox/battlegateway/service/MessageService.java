package ru.abradox.battlegateway.service;

import java.util.UUID;

public interface MessageService {

    boolean checkUser(String userName, UUID userId);

    void handleUserMessage(UUID userId, String userMessage);
}
