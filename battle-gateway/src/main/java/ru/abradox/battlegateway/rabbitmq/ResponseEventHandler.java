package ru.abradox.battlegateway.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.abradox.battlegateway.service.ConnectionService;
import ru.abradox.platformapi.battle.BotWrapper;
import ru.abradox.platformapi.cardgame.event.ServerResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseEventHandler {

    private final ConnectionService connectionService;

    @RabbitListener(queues = "bot-response")
    public void processBotResponse(BotWrapper<ServerResponse> botResponse) {
        connectionService.sendMessageToBot(botResponse.getToken(), botResponse.getData());
    }
}