package ru.abradox.statisticservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.abradox.statisticservice.service.RoundService;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RoundManager {

    private final RoundService roundService;

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.SECONDS)
    @SchedulerLock(name = "startDevRounds", lockAtMostFor = "9s", lockAtLeastFor = "9s")
    public void startDevRounds() {
        roundService.startDevRounds();
    }

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.SECONDS)
    @SchedulerLock(name = "startProdRounds", lockAtMostFor = "9s", lockAtLeastFor = "9s")
    public void startProdRounds() {
        roundService.startProdRounds();
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    @SchedulerLock(name = "startCompetition", lockAtMostFor = "58s", lockAtLeastFor = "58s")
    public void startCompetition() {
        roundService.startCompetition();
    }

    @Scheduled(fixedDelay = 3, timeUnit = TimeUnit.MINUTES)
    @SchedulerLock(name = "validateRounds", lockAtMostFor = "2m", lockAtLeastFor = "2m")
    public void validateRounds() {
        roundService.validateRounds();
    }
}
