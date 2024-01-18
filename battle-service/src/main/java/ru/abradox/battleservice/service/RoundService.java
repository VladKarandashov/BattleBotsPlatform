package ru.abradox.battleservice.service;

import ru.abradox.platformapi.battle.event.StartRound;
import ru.abradox.platformapi.battle.event.WantedRound;
import ru.abradox.platformapi.cardgame.event.BotAction;

import java.util.UUID;

public interface RoundService {

    void startRound(StartRound startRoundEvent);

    void wantedRound(WantedRound wantedRoundEvent);

    void action(UUID token, BotAction action);
}
