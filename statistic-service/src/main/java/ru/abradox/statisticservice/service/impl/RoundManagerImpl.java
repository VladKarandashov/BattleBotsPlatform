package ru.abradox.statisticservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.abradox.statisticservice.service.RoundManager;
import ru.abradox.statisticservice.service.RoundService;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoundManagerImpl implements RoundManager {

    private final RoundService roundService;

    @Override
    @SchedulerLock(name = "startDevRounds", lockAtMostFor = "9s", lockAtLeastFor = "9s")
    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.SECONDS)
    public void startDevRounds() {
        log.info("Запустил процесс создания новых DEV партий");
        roundService.startDevRounds();
        log.info("Завершил процесс создания новых DEV партий");
    }
}