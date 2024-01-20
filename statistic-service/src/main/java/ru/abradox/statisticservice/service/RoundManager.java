package ru.abradox.statisticservice.service;

@SuppressWarnings("unused") // выполняется scheduler'ом по расписанию
public interface RoundManager {

    void startDevRounds();

    void startProdRounds();

    void startCompetition();

    void validateRounds();
}
