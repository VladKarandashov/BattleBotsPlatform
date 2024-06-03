package ru.abradox.platformapi.cardgame;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {

    private Integer number; // от 6 до 14

    private Integer suit; // от 1 до 4

    /**
     * Может ли эта карта побить переданную?
     */
    public boolean canBeat(CardDto card, Integer trumpSuit) {
        if (this.suit.equals(card.getSuit())) {
            // Если масть одинаковая, проверяем, что отбивающая карта старше
            return this.number > card.getNumber();
        }
        // Если отбиваемая карта - козырь
        return this.suit.equals(trumpSuit);
        // В остальных случаях побить не можем
    }
}
