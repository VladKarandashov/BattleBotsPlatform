package ru.abradox.battleservice.service;

import ru.abradox.battleservice.model.RoundState;
import ru.abradox.platformapi.battle.event.StartRound;
import ru.abradox.platformapi.cardgame.event.BotAction;

import java.util.UUID;

public interface GameService {

    void startRound(StartRound startRoundEvent);

    void doAction(RoundState round, UUID token, BotAction botAction);
}
