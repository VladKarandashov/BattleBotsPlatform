package ru.abradox.statisticservice.service;

import ru.abradox.platformapi.battle.event.FinishRound;
import ru.abradox.platformapi.statistic.CompetitionInfo;

public interface RoundService {

    void startDevRounds();

    void startProdRounds();

    void startCompetition();

    void finishRound(FinishRound finishRoundRequest);

    void validateRounds();

    CompetitionInfo getCompetitionInfo();

    CompetitionInfo getHistoryInfo();
}
