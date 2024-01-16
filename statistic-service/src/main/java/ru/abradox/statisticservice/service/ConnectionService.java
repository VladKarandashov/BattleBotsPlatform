package ru.abradox.statisticservice.service;

import ru.abradox.client.token.TokenDto;

import java.util.List;

public interface ConnectionService {

    void updateActiveConnections(List<TokenDto> activeTokens);
}
