package ru.abradox.statisticservice.service;


import ru.abradox.platformapi.token.TokenDto;

import java.util.List;

public interface ConnectionService {

    void updateActiveConnections(List<TokenDto> activeTokens);
}
