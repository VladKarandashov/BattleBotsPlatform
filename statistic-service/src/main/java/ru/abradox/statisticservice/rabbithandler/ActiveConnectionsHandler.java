package ru.abradox.statisticservice.rabbithandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abradox.client.token.TokenDto;
import ru.abradox.statisticservice.entity.BotEntity;
import ru.abradox.statisticservice.repository.BotRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActiveConnectionsHandler {

    private final BotRepository botRepository;

    @Transactional
    @RabbitListener(queues = "active-connections")
    public void processAllMessages(List<TokenDto> activeTokens) {
        log.info("Обновляю активность ботов");
        var activeTokensIdSet = activeTokens.stream().map(TokenDto::getId).map(UUID::fromString).collect(Collectors.toSet());
        botRepository.setActiveStatusesByActiveTokens(activeTokensIdSet);
        var existTokens = botRepository.findAllTokens();
        var newBots = activeTokens.stream()
                .filter(token -> !existTokens.contains(token.getUid()))
                .map(token -> BotEntity
                        .builder()
                        .userId(token.getUserId())
                        .token(token.getUid())
                        .type(token.getType())
                        .isActive(true)
                        .isPlay(false)
                        .build()
                ).toList();
        botRepository.saveAll(newBots);
    }
}
