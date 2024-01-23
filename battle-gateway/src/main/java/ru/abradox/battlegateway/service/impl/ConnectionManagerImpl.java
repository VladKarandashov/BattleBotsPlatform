package ru.abradox.battlegateway.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.abradox.battlegateway.client.token.TokenHolder;
import ru.abradox.battlegateway.service.ConnectionManager;
import ru.abradox.battlegateway.service.ConnectionService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectionManagerImpl implements ConnectionManager {

    private final TokenHolder tokenHolder;
    private final RabbitTemplate rabbitTemplate;
    private final ConnectionService connectionService;

    @Override
    @Async
    @Scheduled(fixedRate = 2 * 1000)
    public void clearConnectionsByTokens() {
        var connections = connectionService.getConnections();
        connections.forEach((uuid, session) -> {
            if (tokenHolder.isNotTokenExist(uuid)) {
                connectionService.closeConnection(uuid);
            }
        });
        log.debug("Подключено {} ботов", connections.size());
    }

    @Override
    @Async
    @Scheduled(fixedRate = 3 * 1000)
    public void processActiveConnections() {
        var connections = connectionService.getConnections();
        var activeTokens = connections.keySet()
                .stream()
                .map(tokenHolder::getTokenInfo)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        log.debug("Послал информацию о {} активных подключениях", activeTokens.size());
        rabbitTemplate.convertAndSend("active-connections", "", activeTokens);
    }
}
