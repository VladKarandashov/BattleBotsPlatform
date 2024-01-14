package ru.abradox.battlegateway.service;

import java.util.UUID;

public interface MessageService {

    boolean checkUser(String botName, String botToken);

    void handleUserMessage(UUID botToken, String userMessage);
}
