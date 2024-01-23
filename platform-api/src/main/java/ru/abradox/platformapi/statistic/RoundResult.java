package ru.abradox.platformapi.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoundResult extends RoundInfo {

    private Boolean isDownBotWin;

    public RoundResult(Integer downBotId, Integer topBotId, Long downBotWinCount, Long topBotWinCount, Boolean isDownBotWin) {
        super(downBotId, topBotId, downBotWinCount, topBotWinCount);
        this.isDownBotWin = isDownBotWin;
    }
}