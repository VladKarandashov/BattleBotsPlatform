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
    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.SECONDS)
    @SchedulerLock(name = "startDevRounds", lockAtMostFor = "9s", lockAtLeastFor = "9s")
    public void startDevRounds() {
        log.info("Запустил процесс создания новых DEV партий");
        roundService.startDevRounds();
        log.info("Завершил процесс создания новых DEV партий");
    }

    @Override
    @Scheduled(fixedDelay = 3, timeUnit = TimeUnit.MINUTES)
    @SchedulerLock(name = "validateRounds", lockAtMostFor = "2m", lockAtLeastFor = "2m")
    public void validateRounds() {
        log.info("Начинаю валидацию партий");
        roundService.validateRounds();
        log.info("Завершил валидацию партий");
    }
}
