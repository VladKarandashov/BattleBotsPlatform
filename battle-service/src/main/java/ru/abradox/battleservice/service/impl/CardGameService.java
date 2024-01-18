package ru.abradox.battleservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.abradox.battleservice.exception.ActionException;
import ru.abradox.battleservice.model.RoundRepository;
import ru.abradox.battleservice.model.RoundState;
import ru.abradox.battleservice.service.GameService;
import ru.abradox.battleservice.utils.LockExecutor;
import ru.abradox.platformapi.battle.BotWrapper;
import ru.abradox.platformapi.battle.event.StartRound;
import ru.abradox.platformapi.cardgame.CardDto;
import ru.abradox.platformapi.cardgame.event.ActionCode;
import ru.abradox.platformapi.cardgame.event.BotAction;
import ru.abradox.platformapi.cardgame.event.ServerResponse;
import ru.abradox.platformapi.cardgame.event.StatusCode;

import java.security.SecureRandom;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardGameService implements GameService {

    private final RoundRepository roundRepository;
    private final RabbitTemplate rabbitTemplate;
    private final LockExecutor lockExecutor;

    @Override
    public void startRound(StartRound startRoundEvent) {
        var round = createRound(startRoundEvent);
        round = roundRepository.save(round);
        var infoMap = round.getStateInfo();
        infoMap.forEach((token, value) -> {
            var serverResponse = value.getIsNeedAction() ?
                    new ServerResponse(StatusCode.START_ROUND_WITH_ACTIVE, value) :
                    new ServerResponse(StatusCode.START_ROUND, value);
            var response = new BotWrapper<>(token, serverResponse);
            rabbitTemplate.convertAndSend("bot-response", "", response);
        });
    }

    @Override
    public void action(UUID token, BotAction action) {
        try {
            // Проверить раунд на существование и что он не закончен
            var roundId = action.getRoundId();
            var round = checkRoundExist(roundId);
            lockExecutor.execute(round.toString(), 3, () -> {
                // Проверить что мы ждём ход именно этого бота.
                // Проверить, что он правильно выбрал линии атаки/защиты по своему статусу (или он сдался).
                checkActionAllowed(round, token, action.getCode());
                // Далее уже конкретные действия
                doAction(round, token, action);
            });
        } catch (ActionException e) {
            var response = new BotWrapper<>(token, e.getResponse());
            rabbitTemplate.convertAndSend("bot-response", "", response);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка", e);
            var response = new BotWrapper<>(token, new ServerResponse(StatusCode.INTERNAL_ERROR));
            rabbitTemplate.convertAndSend("bot-response", "", response);
        }
    }

    private RoundState createRound(StartRound event) {
        var cardList = generateAllCardList();
        var topBotCards = cardList.subList(0, 6);
        var downBotCards = cardList.subList(6, 12);
        var deck = cardList.subList(12, cardList.size());
        return new RoundState(
                event.getId(), event.getType(), event.getTopBotToken(),
                event.getDownBotToken(), deck, topBotCards, downBotCards);
    }

    private List<CardDto> generateAllCardList() {
        var cardList = new ArrayList<CardDto>();
        for (int number = 6; number <= 14; number++) {
            for (int suit = 1; suit <= 4; suit++) {
                cardList.add(new CardDto(number, suit));
            }
        }
        var random = new SecureRandom();
        Collections.shuffle(cardList, random);
        return cardList;
    }

    private RoundState checkRoundExist(UUID roundId) {
        var round = roundRepository.findById(roundId)
                .orElseThrow(() -> new ActionException(new ServerResponse(StatusCode.ROUND_NOT_FOUND)));
        if (round.getResult() != null) {
            throw new ActionException(new ServerResponse(StatusCode.ROUND_ALREADY_FINISHED));
        }
        return round;
    }

    private void checkActionAllowed(RoundState round, UUID token, ActionCode actionCode) {
        var activeToken = round.getActiveToken();
        if (!activeToken.equals(token)) {
            throw new ActionException(new ServerResponse(StatusCode.NOT_YOUR_TURN));
        }
        if (ActionCode.attackerActions.contains(actionCode) && !round.isAttacker(token)) {
            throw new ActionException(new ServerResponse(StatusCode.WRONG_ACTION_TYPE));
        }
        if (ActionCode.defenderActions.contains(actionCode) && !round.isDefender(token)) {
            throw new ActionException(new ServerResponse(StatusCode.WRONG_ACTION_TYPE));
        }
    }

    private void doAction(RoundState round, UUID token, BotAction botAction) {
        switch (botAction.getCode().getCode()) {
            case 1: attackAction(round, token, botAction.getCards()); break;
            case 2: discardAction(round, token); break;
            case 3: defendAction(round, token, botAction.getCards()); break;
            case 4: takeAction(round, token); break;
            case 5: giveUpAction(round, token); break;
        }
    }

    private void attackAction(RoundState round, UUID token, Set<CardDto> cards) {
        // проверить чтобы на столе не оказалось больше 6 карт
        // проверить, чтобы на столе не оказалось больше карт, чем есть на руках у второго игрока
        // проверить, что на столе не было карт совсем, либо новые карты подражают старым

        // сделать атаку в картах
        // update time
        // переставить активность

        // отправить защищаемуся, что он должен защищаться
        // отправить атакующему, что он молодец и может ждать хода соперника
    }

    private void discardAction(RoundState round, UUID token) {
        // проверить, что на столе есть карты

        // сбросить карты в бито
        // раздать игрокам карты в нужном порядке

        // ПРОВЕРИТЬ НА ЗАВЕРШЁННОСТЬ

        // update time
        // переставить активность

        // отправить тому кто должен атаковать, что можно атаковать
        // отправить тому кто защищается, что он будет защищаться
    }

    private void defendAction(RoundState round, UUID token, Set<CardDto> cards) {
        // проверить, что этими картами можно отбиться

        // сделать отбитие в картах

        // update time
        // переставить активность
        // отправить тому кто атаковал, что можно подкидывать или БИТО
        // отправить тому кто защищается, что он ждёт хода атакующего
    }

    private void takeAction(RoundState round, UUID token) {
        // проверить, что на столе есть карты

        // переложить карты со стола к защищаемуся игроку
        // раздать игрокам карты в нужном порядке

        // ПРОВЕРИТЬ НА ЗАВЕРШЁННОСТЬ

        // update time

        // отправить тому кто атаковал, что можно атаковать снова
        // отправить тому кто защищается, что он снова ждёт хода атакующего
    }

    private void giveUpAction(RoundState round, UUID token) {
        // ЗАВЕРШИТЬ ПАРТИЮ

        // отправить игрокам результаты партии
    }
}
