package ru.abradox.battlegateway.service;

public interface ConnectionManager {

    void clearConnectionsByTokens();

    void processActiveConnections();
}
