package ru.abradox.statisticservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abradox.platformapi.token.TokenDto;
import ru.abradox.statisticservice.model.entity.BotEntity;
import ru.abradox.statisticservice.model.repository.BotRepository;
import ru.abradox.statisticservice.service.ConnectionService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    private final BotRepository botRepository;

    @Override
    @Transactional
    public void updateActiveConnections(List<TokenDto> activeTokens) {
        var activeTokensIdSet = activeTokens.stream().map(TokenDto::getId).map(UUID::fromString).collect(Collectors.toSet());
        botRepository.setActiveStatusesByActiveTokens(activeTokensIdSet);
        var existTokens = botRepository.findAllTokens();
        var newBots = activeTokens.stream()
                .filter(token -> !existTokens.contains(token.getUid()))
                .map(token -> BotEntity
                        .builder()
                        .userId(token.getUserId())
                        .title(token.getTitle())
                        .token(token.getUid())
                        .type(token.getType())
                        .isActive(true)
                        .isPlay(false)
                        .build()
                ).toList();
        botRepository.saveAll(newBots);
    }
}
