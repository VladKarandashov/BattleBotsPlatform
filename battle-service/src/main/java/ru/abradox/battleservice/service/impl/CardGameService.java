package ru.abradox.battleservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.abradox.battleservice.exception.ActionException;
import ru.abradox.battleservice.model.RoundRepository;
import ru.abradox.battleservice.model.RoundState;
import ru.abradox.battleservice.service.GameService;
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

    @Override
    public void doAction(RoundState round, UUID token, BotAction botAction) {
        // Проверить что мы ждём ход именно этого бота.
        // Проверить, что он правильно выбрал линии атаки/защиты по своему статусу (или он сдался).
        var action = botAction.getCode();
        checkActionAllowed(round, token, action);
        // Далее уже конкретные действия
        switch (action.getCode()) {
            case 1:
                attackAction(round, token, botAction.getCards());
                break;
            case 2:
                discardAction(round, token);
                break;
            case 3:
                defendAction(round, token, botAction.getCards());
                break;
            case 4:
                takeAction(round, token);
                break;
            case 5:
                giveUpAction(round, token);
                break;
        }
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

    private void attackAction(RoundState round, UUID token, Set<CardDto> cards) {
        // проверить что карт не 0
        // проверить чтобы на столе не оказалось больше 6 карт
        // проверить, чтобы на столе не оказалось больше карт, чем есть на руках у второго игрока
        // проверить что карты у него есть
        // проверить, что на столе не было карт совсем и карты одинаковые, либо новые карты подражают старым

        // сделать атаку в картах
        // update time
        // переставить активность

        // отправить защищающемуся, что он должен защищаться
        // отправить атакующему, что он молодец и может ждать хода соперника
    }

    private void discardAction(RoundState round, UUID token) {
        // проверить, что на столе есть карты

        // сбросить карты в бито
        // раздать игрокам карты в нужном порядке

        // ПРОВЕРИТЬ НА ЗАВЕРШЁННОСТЬ (0 карт на руках)

        // update time
        // переставить активность

        // отправить тому кто должен атаковать, что можно атаковать
        // отправить тому кто защищается, что он будет защищаться
    }

    private void defendAction(RoundState round, UUID token, Set<CardDto> cards) {
        // проверить что карты у него есть
        // проверить, что этими картами можно отбиться без дублирования

        // сделать отбитие в картах

        // update time
        // переставить активность
        // отправить тому кто атаковал, что можно подкидывать или БИТО
        // отправить тому кто защищается, что он ждёт хода атакующего
    }

    private void takeAction(RoundState round, UUID token) {
        // переложить карты со стола к защищающемуся игроку
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
