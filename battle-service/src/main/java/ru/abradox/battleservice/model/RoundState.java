package ru.abradox.battleservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import ru.abradox.platformapi.battle.ResultRound;
import ru.abradox.platformapi.battle.TypeRound;
import ru.abradox.platformapi.cardgame.CardDto;
import ru.abradox.platformapi.cardgame.TableDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RedisHash(value = "round_state", timeToLive = 60*60L)
public class RoundState {

    @Id
    private UUID id;

    private TypeRound type;

    private UUID topBotToken;

    private UUID downBotToken;

    private UUID activeToken;

    private ResultRound result;

    private CardDto lastCard;

    private List<CardDto> deck;

    private Set<CardDto> topBotCards;

    private Set<CardDto> downBotCards;

    private Set<TableDto> table;

    public RoundState(UUID id, TypeRound type, UUID topBotToken, UUID downBotToken, List<CardDto> deck,
                      List<CardDto> topBotCards, List<CardDto> downBotCards) {
        this.id = id;
        this.type = type;
        this.topBotToken = topBotToken;
        this.downBotToken = downBotToken;
        this.activeToken = downBotToken;
        this.lastCard = deck.getLast();
        this.deck = deck;
        this.topBotCards = new HashSet<>(topBotCards);
        this.downBotCards = new HashSet<>(downBotCards);
    }
}