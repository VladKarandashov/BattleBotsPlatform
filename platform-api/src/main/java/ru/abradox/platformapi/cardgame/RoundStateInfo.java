package ru.abradox.platformapi.cardgame;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoundStateInfo {

    private UUID id;

    private Set<TableDto> table;

    private Set<CardDto> handCards;

    private CardDto lastCard;

    private Integer cardLeft; // сколько карт осталось в колоде, включая lastCard

    private Integer opponentLeft; // сколько карт сейчас на руках у соперника

    private Boolean isNeedAction;

    private Integer numberOfCardsToAttack; // если ход атакующий - то можно положить на стол не более карт = min(6-table.size, opponentLeft, handCards.size)
}
