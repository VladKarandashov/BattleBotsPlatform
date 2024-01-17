package ru.abradox.battleservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import ru.abradox.platformapi.battle.ResultRound;
import ru.abradox.platformapi.battle.TypeRound;
import ru.abradox.platformapi.cardgame.CardDto;
import ru.abradox.platformapi.cardgame.TableDto;
import ru.abradox.platformapi.cardgame.RoundStateInfo;

import java.time.LocalDateTime;
import java.util.*;

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

    private CardDto lastCard;

    private List<CardDto> deck;

    private Set<CardDto> topBotCards;

    private Set<CardDto> downBotCards;

    private Set<TableDto> table;

    @Indexed
    private LocalDateTime updateTime;

    @Indexed
    private ResultRound result;

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
        this.updateTime = LocalDateTime.now();
    }

    public Map<UUID, RoundStateInfo> getStateInfo() {
        return Map.of(
                topBotToken, getTopStateInfo(),
                downBotToken, getDownStateInfo()
        );
    }

    public RoundStateInfo getTopStateInfo() {
         return getStateInfo(topBotCards, downBotCards.size(), topBotToken.equals(activeToken));
    }

    public RoundStateInfo getDownStateInfo() {
        return getStateInfo(downBotCards, topBotCards.size(), downBotToken.equals(activeToken));
    }

    private RoundStateInfo getStateInfo(Set<CardDto> botCards, Integer opponentLeft, Boolean isNeedAction) {
        return new RoundStateInfo(id, table, botCards, lastCard, deck.size(), opponentLeft, isNeedAction);
    }
}