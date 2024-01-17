package ru.abradox.battleservice.service;

import ru.abradox.platformapi.battle.event.StartRound;
import ru.abradox.platformapi.battle.event.WantedRound;

public interface RoundService {

    void startRound(StartRound startRoundEvent);

    void wantedRound(WantedRound wantedRoundEvent);
}
