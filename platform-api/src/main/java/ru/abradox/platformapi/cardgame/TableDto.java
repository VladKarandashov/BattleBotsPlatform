package ru.abradox.platformapi.cardgame;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableDto {

    private CardDto placed;

    private CardDto beaten;

    public TableDto(CardDto placed) {
        this.placed = placed;
        this.beaten = null;
    }
}
