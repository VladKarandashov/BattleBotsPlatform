package ru.abradox.battleservice.rabbithandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.abradox.battleservice.service.RoundService;
import ru.abradox.platformapi.battle.event.StartRound;
import ru.abradox.platformapi.battle.event.WantedRound;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticEventHandler {

    private final RoundService roundService;

    @RabbitListener(queues = "start-round")
    public void startRound(StartRound startRoundEvent) {
        log.info("Начинаю новый раунд {}", startRoundEvent);
        roundService.startRound(startRoundEvent);
    }

    @RabbitListener(queues = "wanted-round")
    public void wantedRound(WantedRound wantedRoundEvent) {
        log.info("Получен запрос на розыск раунда {}", wantedRoundEvent);
        roundService.wantedRound(wantedRoundEvent);
    }
}
