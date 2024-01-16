package ru.abradox.statisticservice.rabbithandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.abradox.client.token.TokenDto;
import ru.abradox.statisticservice.service.ConnectionService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActiveConnectionsHandler {

    private final ConnectionService connectionService;

    @RabbitListener(queues = "active-connections")
    public void updateActiveConnections(List<TokenDto> activeTokens) {
        log.info("Обновляю активность ботов {}", activeTokens);
        connectionService.updateActiveConnections(activeTokens);
    }
}
