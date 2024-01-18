package ru.abradox.battleservice.service;

import ru.abradox.platformapi.battle.event.StartRound;
import ru.abradox.platformapi.cardgame.event.BotAction;

import java.util.UUID;

public interface GameService {

    void startRound(StartRound startRoundEvent);

    void action(UUID token, BotAction action);
}
