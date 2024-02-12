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
import ru.abradox.platformapi.battle.ResultRound;
import ru.abradox.platformapi.battle.StatusRound;
import ru.abradox.platformapi.battle.event.FinishRound;
import ru.abradox.platformapi.battle.event.StartRound;
import ru.abradox.platformapi.cardgame.CardDto;
import ru.abradox.platformapi.cardgame.TableDto;
import ru.abradox.platformapi.cardgame.event.ActionCode;
import ru.abradox.platformapi.cardgame.event.BotAction;
import ru.abradox.platformapi.cardgame.event.ServerResponse;
import ru.abradox.platformapi.cardgame.event.StatusCode;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        sendStateInfo(round, StatusCode.START_ROUND_WITH_ACTIVE, StatusCode.START_ROUND);
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
        ActionCode action = botAction.getCode();
        checkActionAllowed(round, token, action);
        // Далее уже конкретные действия
        switch (action) {
            case ATTACK:
                attackAction(round, token, botAction.getCards());
                break;
            case DISCARD:
                discardAction(round);
                break;
            case DEFEND:
                defendAction(round, token, botAction.getCards());
                break;
            case TAKE:
                takeAction(round, token);
                break;
            case GIVE_UP:
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
        var botState = round.getMyStateInfoByToken(token);
        var opponentState = round.getOpponentStateInfoByToken(token);
        if (cards.isEmpty()) {
            throw new ActionException(new ServerResponse(StatusCode.NO_CARDS));
        }
        if (cards.size() + round.getTable().size() > 6) {
            throw new ActionException(new ServerResponse(StatusCode.TOO_MANY_CARDS_ON_TABLE));
        }
        if (cards.size() > opponentState.getHandCards().size()) {
            throw new ActionException(new ServerResponse(StatusCode.TOO_MANY_CARDS_FOR_OPPONENT));
        }
        if (!botState.getHandCards().containsAll(cards)) {
            throw new ActionException(new ServerResponse(StatusCode.NOT_HAVE_CARDS));
        }
        if (!round.getTable().isEmpty() && !isCardSimilarity(round.getTable(), cards)) {
            throw new ActionException(new ServerResponse(StatusCode.WRONG_CARDS_ATTACK));
        }

        // перекладываем карты от атакующего на стол
        // update time
        // переставить активность
        botState.getHandCards().removeAll(cards);
        var table = round.getTable();
        cards.forEach(card -> table.add(new TableDto(card)));
        round.setUpdateTime(LocalDateTime.now());
        round.changeActivity();
        round = roundRepository.save(round);

        // отправить защищающемуся, что он должен защищаться
        // отправить атакующему, что он молодец и может ждать хода соперника
        sendStateInfo(round, StatusCode.NEED_DEFEND, StatusCode.WAIT_OPPONENT_DEFEND);
    }

    private void discardAction(RoundState round) {
        // проверить, что на столе есть карты
        if (round.getTable().isEmpty()) {
            throw new ActionException(new ServerResponse(StatusCode.EMPTY_DISCARD));
        }

        // удалить карты со стола
        // раздать игрокам карты в нужном порядке
        round.setTable(Set.of());
        round.dealCards();

        // ПРОВЕРИТЬ НА ЗАВЕРШЁННОСТЬ (0 карт на руках)
        if (round.getResult() != null) {
            finishRound(round);
            return;
        }

        // update time
        // переставить активность
        // переставить признак атаки/защиты
        round.setUpdateTime(LocalDateTime.now());
        round.changeActivity();
        round.changeAttacker();
        round = roundRepository.save(round);

        // отправить тому кто должен атаковать, что можно атаковать
        // отправить тому кто защищается, что он будет защищаться
        sendStateInfo(round, StatusCode.NEED_ATTACK_AFTER_DISCARD, StatusCode.WAIT_OPPONENT_ATTACK_AFTER_DISCARD);
    }

    private void defendAction(RoundState round, UUID token, Set<CardDto> cards) {
        // проверить что карты у него есть
        var botState = round.getMyStateInfoByToken(token);
        if (!botState.getHandCards().containsAll(cards)) {
            throw new ActionException(new ServerResponse(StatusCode.NOT_HAVE_CARDS));
        }

        // проверить, что этими картами можно отбиться без дублирования
        // сделать отбитие в картах
        var trumpSuit = round.getLastCard().getSuit();

        for (TableDto tableDto : round.getTable()) {
            if (tableDto.getBeaten() != null) continue;
            var placedCard = tableDto.getPlaced();
            var beatingCard = findBeatingCard(placedCard, cards, trumpSuit)
                    .orElseThrow(() -> new ActionException(new ServerResponse(StatusCode.WRONG_CARDS_DEFEND)));

            tableDto.setBeaten(beatingCard);
            cards.remove(beatingCard); // Удаляем использованную карту
        }

        // update time
        // переставить активность
        round.setUpdateTime(LocalDateTime.now());
        round.changeActivity();
        round = roundRepository.save(round);

        // отправить тому кто атаковал, что можно подкидывать или БИТО
        // отправить тому кто защищается, что он ждёт хода атакующего
        sendStateInfo(round, StatusCode.NEED_ATTACK_OR_DISCARD, StatusCode.WAIT_OPPONENT_ATTACK_OR_DISCARD);
    }

    private void takeAction(RoundState round, UUID token) {
        // переложить карты со стола к защищающемуся игроку
        // раздать игрокам карты в нужном порядке
        var defender = round.getMyStateInfoByToken(token);
        var tableCards = round.getTable().stream()
                .flatMap(tableDto -> Stream.of(tableDto.getPlaced(), tableDto.getBeaten()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        defender.getHandCards().addAll(tableCards);
        round.setTable(Set.of());
        round.dealCards();


        // ПРОВЕРИТЬ НА ЗАВЕРШЁННОСТЬ
        if (round.getResult() != null) {
            finishRound(round);
            return;
        }

        // update time
        // переставить активность
        round.setUpdateTime(LocalDateTime.now());
        round.changeActivity();
        round = roundRepository.save(round);

        // отправить тому кто атаковал, что можно атаковать снова
        // отправить тому кто защищается, что он снова ждёт хода атакующего
        sendStateInfo(round, StatusCode.NEED_ATTACK_AFTER_TAKE, StatusCode.WAIT_OPPONENT_ATTACK_AFTER_TAKE);
    }

    private void giveUpAction(RoundState round, UUID token) {
        // ЗАВЕРШИТЬ ПАРТИЮ
        if (token.equals(round.getTopBotToken())) {
            round.setStatus(StatusRound.FINISHED);
            round.setResult(ResultRound.DOWN);
        } else {
            round.setStatus(StatusRound.FINISHED);
            round.setResult(ResultRound.TOP);
        }
        finishRound(round);
    }

    private boolean isCardSimilarity(Set<TableDto> table, Set<CardDto> desiredCards) {
        var existOnTableCardNumbers = table.stream()
                .flatMap(tableDto -> Stream.of(tableDto.getPlaced(), tableDto.getBeaten()))
                .filter(Objects::nonNull)
                .map(CardDto::getNumber)
                .collect(Collectors.toSet());
        return desiredCards.stream().map(CardDto::getNumber).allMatch(existOnTableCardNumbers::contains);
    }

    private void finishRound(RoundState round) {
        round = roundRepository.save(round);
        var result = round.getResult();
        var topBotToken = round.getTopBotToken();
        var downBotToken = round.getDownBotToken();

        if (ResultRound.TOP.equals(result)) {
            rabbitTemplate.convertAndSend("bot-response", "",
                    new BotWrapper<>(topBotToken, new ServerResponse(StatusCode.WIN)));
            rabbitTemplate.convertAndSend("bot-response", "",
                    new BotWrapper<>(downBotToken, new ServerResponse(StatusCode.LOST)));
        } else if (ResultRound.DOWN.equals(result)) {
            rabbitTemplate.convertAndSend("bot-response", "",
                    new BotWrapper<>(topBotToken, new ServerResponse(StatusCode.LOST)));
            rabbitTemplate.convertAndSend("bot-response", "",
                    new BotWrapper<>(downBotToken, new ServerResponse(StatusCode.WIN)));
        } else if (ResultRound.DRAW.equals(result)) {
            rabbitTemplate.convertAndSend("bot-response", "",
                    new BotWrapper<>(topBotToken, new ServerResponse(StatusCode.DRAW)));
            rabbitTemplate.convertAndSend("bot-response", "",
                    new BotWrapper<>(downBotToken, new ServerResponse(StatusCode.DRAW)));
        }

        log.info("Партия завершилась {}", result);
        rabbitTemplate.convertAndSend("finish-round", "", new FinishRound(round.getId(), result));
    }

    private void sendStateInfo(RoundState round, StatusCode activeStatus, StatusCode notActiveStatus) {
        var infoMap = round.getStateInfo();
        infoMap.forEach((tokenId, info) -> {
            var serverResponse = info.getIsNeedAction() ?
                    new ServerResponse(activeStatus, info) : // это активный (от которого ждём хода)
                    new ServerResponse(notActiveStatus, info); // это не активный (который ждёт)
            var response = new BotWrapper<>(tokenId, serverResponse);
            rabbitTemplate.convertAndSend("bot-response", "", response);
        });
    }

    private Optional<CardDto> findBeatingCard(CardDto placedCard, Set<CardDto> cards, Integer trumpSuit) {
        return cards.stream()
                .filter(c -> c.canBeat(placedCard, trumpSuit))
                .min(Comparator.comparingInt(
                        c -> c.getSuit().equals(trumpSuit) ? c.getNumber() + 200 : c.getNumber()));
        // Стратегия минимизации: выбираем наименьшую подходящую карту, приоритет у козырей.
    }
}
