package ru.abradox.battleservice.rabbithandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.abradox.battleservice.service.RoundService;
import ru.abradox.platformapi.battle.BotWrapper;
import ru.abradox.platformapi.cardgame.event.BotAction;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayEventHandler {

    private final RoundService roundService;

    @RabbitListener(queues = "bot-actions")
    public void action(BotWrapper<BotAction> actionEvent) {
        roundService.action(actionEvent.getToken(), actionEvent.getData());
    }
}
