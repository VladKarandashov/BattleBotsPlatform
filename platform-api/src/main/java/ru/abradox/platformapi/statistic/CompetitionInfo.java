package ru.abradox.platformapi.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionInfo {

    private List<Integer> orderedBots;

    private List<RoundInfo> roundResults;

    private List<HistoryInfo> history;
}
