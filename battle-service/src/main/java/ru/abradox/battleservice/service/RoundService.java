package ru.abradox.battleservice.service;

import ru.abradox.platformapi.game.event.StartRound;
import ru.abradox.platformapi.game.event.WantedRound;

public interface RoundService {

    void startRound(StartRound startRoundEvent);

    void wantedRound(WantedRound wantedRoundEvent);
}
