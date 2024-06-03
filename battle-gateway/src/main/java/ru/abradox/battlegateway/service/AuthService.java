package ru.abradox.battlegateway.service;

public interface AuthService {

    boolean checkBotAccess(String botName, String botToken);
}
