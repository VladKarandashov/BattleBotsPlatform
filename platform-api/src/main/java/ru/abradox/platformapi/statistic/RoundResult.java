package ru.abradox.platformapi.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoundResult {
    private Integer downBotId;
    private Integer topBotId;
    private Long downBotWinCount;
    private Long topBotWinCount;
    private Boolean isDownBotWin;
}