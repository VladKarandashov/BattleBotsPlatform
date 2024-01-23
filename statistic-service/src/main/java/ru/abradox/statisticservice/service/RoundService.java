package ru.abradox.statisticservice.service;

import ru.abradox.platformapi.battle.event.FinishRound;
import ru.abradox.platformapi.statistic.current.CompetitionInfo;
import ru.abradox.platformapi.statistic.history.HistoryInfo;

import java.util.List;

public interface RoundService {

    void startDevRounds();

    void startProdRounds();

    void startCompetition();

    void finishRound(FinishRound finishRoundRequest);

    void validateRounds();

    CompetitionInfo getCompetitionInfo();

    List<HistoryInfo> getHistoryInfo();
}
