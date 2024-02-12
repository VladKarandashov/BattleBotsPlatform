package ru.abradox.platformapi.statistic.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoundResultInfo {

    private Integer downBotPosition;
    private Integer topBotPosition;
    private Long downBotWinCount;
    private Long topBotWinCount;
    private Boolean isDownBotWin;
}
