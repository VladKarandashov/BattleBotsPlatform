package ru.abradox.battleservice.service;

@SuppressWarnings("unused") // выполняется scheduler
public interface RoundManager {

    void completeOldRounds();
}
