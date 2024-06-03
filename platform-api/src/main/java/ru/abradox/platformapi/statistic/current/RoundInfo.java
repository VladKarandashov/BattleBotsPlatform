package ru.abradox.platformapi.statistic.current;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoundInfo {

    private Integer downBotPosition;
    private Integer topBotPosition;
    private Long downBotWinCount;
    private Long topBotWinCount;
    private Long drawCount;
    private Long leftCount;
    private Boolean isPlay;
}
