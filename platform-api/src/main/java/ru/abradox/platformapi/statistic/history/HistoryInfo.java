package ru.abradox.platformapi.statistic.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryInfo {

    private Integer id;

    private Map<Integer, String> botTitleByPosition;

    private List<RoundResultInfo> result;
}
