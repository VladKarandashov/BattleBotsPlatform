package ru.abradox.statisticservice.service;

import ru.abradox.platformapi.game.event.FinishRound;

public interface RoundService {

    void startDevRounds();

    void finishRound(FinishRound finishRoundRequest);

    void validateRounds();
}
