package ru.abradox.platformapi.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryInfo {

    private Integer id;

    private List<Integer> orderedBots;

    private List<RoundResult> roundResults;
}
