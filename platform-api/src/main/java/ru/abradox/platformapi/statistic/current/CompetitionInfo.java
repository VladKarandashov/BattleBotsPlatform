package ru.abradox.platformapi.statistic.current;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionInfo {

    private Integer id;

    private List<BotInfo> bots;

    private List<RoundInfo> roundInfo;
}
