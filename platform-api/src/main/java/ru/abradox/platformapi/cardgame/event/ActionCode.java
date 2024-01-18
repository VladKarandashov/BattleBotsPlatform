package ru.abradox.platformapi.cardgame.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ActionCode {
    // ходы могут выполняться только в случае если active в RoundState

    // АТАКУЮЩИЕ ХОДЫ (если attacker в RoundState)
    ATTACK(1),            // бросить карты на стол (не более чем 6 и не более чем карт на руках игрока)
    DISCARD(2),           // бито - признать что второй игрок отбился (не работает если стол пуст)
    // ЗАЩИТНЫЕ ХОДЫ (если НЕ attacker в RoundState)
    DEFEND(3),            // отбить карты (не более чем не-отбитых карт на столе)
    TAKE(4),              // взять карты со стола
    // УНИВЕРСАЛЬНЫЕ ХОДЫ
    GIVE_UP(5)            // сдаться
    ;

    private final int code;

    public int getCode() {
        return code;
    }

    public static final Set<ActionCode> attackerActions = Set.of(ATTACK, DISCARD);

    public static final Set<ActionCode> defenderActions = Set.of(DEFEND, TAKE);
}
