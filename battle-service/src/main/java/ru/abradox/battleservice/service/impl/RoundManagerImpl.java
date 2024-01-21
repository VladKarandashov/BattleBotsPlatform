package ru.abradox.battleservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.abradox.battleservice.model.RoundRepository;
import ru.abradox.battleservice.service.RoundManager;
import ru.abradox.battleservice.service.RoundService;
import ru.abradox.platformapi.battle.TypeRound;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoundManagerImpl implements RoundManager {

    private final RoundService roundService;
    private final RoundRepository roundRepository;

    @Override
    @Scheduled(fixedDelay = 2, timeUnit = TimeUnit.SECONDS)
    @SchedulerLock(name = "completeOldRounds", lockAtMostFor = "1s", lockAtLeastFor = "1s")
    public void completeOldRounds() {
        var timeNow = LocalDateTime.now();
        var notFinishedRoundList = roundRepository.findAllByResultIsNull();
        notFinishedRoundList.forEach(round -> {
            var updateTime = round.getUpdateTime();
            var duration = Duration.between(updateTime, timeNow);
            var type = round.getType();
            if (TypeRound.DEV.equals(type) && duration.compareTo(Duration.ofSeconds(90)) >= 0) {
                // DEV ход длиннее 90 секунд - завершаем
                log.info("Завершаю раунд {} в связи с истечением срока хода", round.getId());
                CompletableFuture.runAsync(() -> roundService.completeOldRound(round));
            } else if (TypeRound.PROD.equals(type) && duration.compareTo(Duration.ofSeconds(4)) >= 0) {
                // PROD ход длиннее 90 секунд - завершаем
                log.info("Завершаю раунд {} в связи с истечением срока хода", round.getId());
                CompletableFuture.runAsync(() -> roundService.completeOldRound(round));
            }
        });
    }
}
