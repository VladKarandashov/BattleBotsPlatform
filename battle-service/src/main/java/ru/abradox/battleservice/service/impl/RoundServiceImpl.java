package ru.abradox.battleservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ru.abradox.battleservice.exception.ActionException;
import ru.abradox.battleservice.model.RoundRepository;
import ru.abradox.battleservice.service.GameService;
import ru.abradox.battleservice.service.RoundService;
import ru.abradox.battleservice.utils.LockExecutor;
import ru.abradox.platformapi.battle.BotWrapper;
import ru.abradox.platformapi.battle.ResultRound;
import ru.abradox.platformapi.battle.TypeRound;
import ru.abradox.platformapi.battle.event.FinishRound;
import ru.abradox.platformapi.battle.event.StartRound;
import ru.abradox.platformapi.battle.event.WantedRound;
import ru.abradox.platformapi.cardgame.event.BotAction;
import ru.abradox.platformapi.cardgame.event.ServerResponse;
import ru.abradox.platformapi.cardgame.event.StatusCode;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoundServiceImpl implements RoundService {

    private final RoundRepository roundRepository;
    private final RabbitTemplate rabbitTemplate;
    private final LockExecutor lockExecutor;
    private final GameService gameService;

    @Override
    public void startRound(StartRound startRoundEvent) {
        var roundId = startRoundEvent.getId();
        roundRepository.findById(roundId).ifPresent(round -> wantedRound(new WantedRound(startRoundEvent)));
        gameService.startRound(startRoundEvent);
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
                // если раунд уже закончен, пере-отправляем событие о завершении
                rabbitTemplate.convertAndSend("finish-round", "", new FinishRound(roundId, result));
            }
        }
    }

    @Override
    public void action(UUID token, BotAction action) {
        try {
            // Проверить раунд на существование и что он не закончен
            var roundId = action.getRoundId();
            var round = roundRepository.findById(roundId)
                    .orElseThrow(() -> new ActionException(new ServerResponse(StatusCode.ROUND_NOT_FOUND)));
            if (round.getResult() != null) {
                throw new ActionException(new ServerResponse(StatusCode.ROUND_ALREADY_FINISHED));
            }
            log.info("Новое действие пользователя {} с телом {} для раунда {}", token, action, round);
            lockExecutor.execute(round.toString(), 3, () -> gameService.doAction(round, token, action));
        } catch (ActionException e) {
            log.error("Бизнес ошибка", e);
            var response = new BotWrapper<>(token, e.getResponse());
            rabbitTemplate.convertAndSend("bot-response", "", response);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка", e);
            var response = new BotWrapper<>(token, new ServerResponse(StatusCode.INTERNAL_ERROR));
            rabbitTemplate.convertAndSend("bot-response", "", response);
        }
    }
}
