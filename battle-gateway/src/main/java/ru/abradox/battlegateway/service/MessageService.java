package ru.abradox.battlegateway.service;

import java.util.UUID;

public interface MessageService {

    void handleUserMessage(UUID botToken, String userMessage);
}
