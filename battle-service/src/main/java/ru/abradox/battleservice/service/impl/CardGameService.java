package ru.abradox.battleservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.abradox.battleservice.model.RoundRepository;
import ru.abradox.battleservice.model.RoundState;
import ru.abradox.battleservice.service.GameService;
import ru.abradox.platformapi.battle.event.StartRound;
import ru.abradox.platformapi.cardgame.CardDto;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        // TODO генерация ответов для игроков
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
}
