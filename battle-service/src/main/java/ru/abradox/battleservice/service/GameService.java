package ru.abradox.battleservice.service;

import ru.abradox.platformapi.battle.event.StartRound;

public interface GameService {

    void startRound(StartRound startRoundEvent);
}
