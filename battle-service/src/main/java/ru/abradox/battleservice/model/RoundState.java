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

import static org.apache.commons.lang.math.NumberUtils.min;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RedisHash(value = "round_state", timeToLive = 10*60L)
public class RoundState {

    @Id
    private UUID id;

    private TypeRound type;

    private UUID topBotToken;

    private UUID downBotToken;

    private UUID activeToken; // от кого сейчас ждём ход?

    private UUID attackerToken; // кто сейчас в позиции атакующего

    private CardDto lastCard;

    private List<CardDto> deck = new ArrayList<>();

    private Set<CardDto> topBotCards = new HashSet<>();

    private Set<CardDto> downBotCards = new HashSet<>();

    private Set<TableDto> table = new HashSet<>();

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
        this.attackerToken = downBotToken;
        this.lastCard = deck.get(deck.size()-1);
        this.deck = deck;
        this.topBotCards = new HashSet<>(topBotCards);
        this.downBotCards = new HashSet<>(downBotCards);
        this.table = new HashSet<>();
        this.updateTime = LocalDateTime.now();
    }

    public void changeActivity() {
        activeToken = activeToken.equals(topBotToken) ? downBotToken : topBotToken;
    }

    public void changeAttacker() {
        attackerToken = attackerToken.equals(topBotToken) ? downBotToken : topBotToken;
    }

    public void dealCards() {
        var attacker = getAttackerStateInfo();
        while (attacker.getHandCards().size() < 6 && !deck.isEmpty()) {
            attacker.getHandCards().add(deck.remove(0));
        }
        var defender = getDefenderStateInfo();
        while (defender.getHandCards().size() < 6 && !deck.isEmpty()) {
            defender.getHandCards().add(deck.remove(0));
        }

        if (attacker.getHandCards().isEmpty() && defender.getHandCards().isEmpty()) {
            result = ResultRound.DRAW;
        } else if (attacker.getHandCards().isEmpty()) {
            result = isAttacker(topBotToken) ? ResultRound.TOP : ResultRound.DOWN;
        } else if (defender.getHandCards().isEmpty()) {
            result = isDefender(topBotToken) ? ResultRound.TOP : ResultRound.DOWN;
        }
    }

    public boolean isAttacker(UUID token) {
        return attackerToken.equals(token);
    }

    public boolean isDefender(UUID token) {
        return !isAttacker(token);
    }

    public Map<UUID, RoundStateInfo> getStateInfo() {
        return Map.of(
                topBotToken, getTopStateInfo(),
                downBotToken, getDownStateInfo()
        );
    }

    public RoundStateInfo getAttackerStateInfo() {
        return topBotToken.equals(attackerToken) ? getTopStateInfo() : getDownStateInfo();
    }

    public RoundStateInfo getDefenderStateInfo() {
        return topBotToken.equals(attackerToken) ? getDownStateInfo() : getTopStateInfo();
    }

    public RoundStateInfo getMyStateInfoByToken(UUID token) {
        return topBotToken.equals(token) ? getTopStateInfo() : getDownStateInfo();
    }

    public RoundStateInfo getOpponentStateInfoByToken(UUID token) {
        return topBotToken.equals(token) ? getDownStateInfo() : getTopStateInfo();
    }

    public RoundStateInfo getTopStateInfo() {
         return getStateInfo(topBotCards, downBotCards.size(), topBotToken.equals(activeToken));
    }

    public RoundStateInfo getDownStateInfo() {
        return getStateInfo(downBotCards, topBotCards.size(), downBotToken.equals(activeToken));
    }

    private RoundStateInfo getStateInfo(Set<CardDto> botCards, Integer opponentLeft, Boolean isNeedAction) {
        return new RoundStateInfo(id, table, botCards, lastCard, deck.size(), opponentLeft, isNeedAction, min(6-table.size(), opponentLeft, botCards.size()));
    }
}