package ru.abradox.statisticservice.service;

import ru.abradox.platformapi.battle.event.FinishRound;

public interface RoundService {

    void startDevRounds();

    void startProdRounds();

    void finishRound(FinishRound finishRoundRequest);

    void validateRounds();
}
