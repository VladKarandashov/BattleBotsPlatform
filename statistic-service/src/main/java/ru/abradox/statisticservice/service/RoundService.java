package ru.abradox.statisticservice.service;

import ru.abradox.platformapi.game.FinishRound;

public interface RoundService {

    void startDevRounds();

    void finishRound(FinishRound finishRound);
}
