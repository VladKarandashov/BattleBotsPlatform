package ru.abradox.battleservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ru.abradox.battleservice.model.RoundRepository;
import ru.abradox.battleservice.service.RoundService;
import ru.abradox.platformapi.game.ResultRound;
import ru.abradox.platformapi.game.TypeRound;
import ru.abradox.platformapi.game.event.FinishRound;
import ru.abradox.platformapi.game.event.StartRound;
import ru.abradox.platformapi.game.event.WantedRound;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoundServiceImpl implements RoundService {

    private final RoundRepository roundRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void startRound(StartRound startRoundEvent) {

    }

    @Override
    public void wantedRound(WantedRound wantedRoundEvent) {
        var roundId = wantedRoundEvent.getId();
        var roundOpt = roundRepository.findById(roundId);
        if (roundOpt.isEmpty()) {
            // раунд не найден
            var typeRound = wantedRoundEvent.getType();
            if (TypeRound.DEV.equals(typeRound)) {
                // если не найден DEV раунд - просто отправим событие о ничьей
                rabbitTemplate.convertAndSend("finish-round", "", new FinishRound(roundId, ResultRound.DRAW));
            } else if (TypeRound.PROD.equals(typeRound)) {
                // если не найден PROD раунд - создадим его
                startRound(wantedRoundEvent);
            }
        } else {
            // раунд найден
            var round = roundOpt.get();
            // посмотрим на результат раунда
            var result = round.getResult();
            if (result != null) {
                // раунд уже закончен, пере-отправляем событие о завершении
                rabbitTemplate.convertAndSend("finish-round", "", new FinishRound(roundId, result));
            }
        }
    }
}
