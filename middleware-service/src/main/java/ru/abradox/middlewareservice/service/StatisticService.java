package ru.abradox.middlewareservice.service;

import ru.abradox.platformapi.statistic.current.CompetitionInfo;
import ru.abradox.platformapi.statistic.history.HistoryInfo;

import java.util.List;

public interface StatisticService {

    CompetitionInfo getCompetitionInfo();

    List<HistoryInfo> getHistoryInfo();
}
