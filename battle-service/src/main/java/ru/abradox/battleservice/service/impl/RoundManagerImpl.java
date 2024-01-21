package ru.abradox.battleservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.abradox.battleservice.model.RoundRepository;
import ru.abradox.battleservice.service.RoundManager;
import ru.abradox.battleservice.service.RoundService;
import ru.abradox.platformapi.battle.TypeRound;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class RoundManagerImpl implements RoundManager {

    private final RoundService roundService;
    private final RoundRepository roundRepository;

    @Override
    // TODO scheduler
    public void completeOldRounds() {
        var timeNow = LocalDateTime.now();
        var notFinishedRoundList = roundRepository.findAllByResultIsNull();
        notFinishedRoundList.forEach(round -> {
            var updateTime = round.getUpdateTime();
            var duration = Duration.between(updateTime, timeNow);
            var type = round.getType();
            if (TypeRound.DEV.equals(type) && duration.compareTo(Duration.ofSeconds(90)) >= 0) {
                // DEV ход длиннее 90 секунд - завершаем
                CompletableFuture.runAsync(() -> roundService.completeOldRound(round));
            } else if (TypeRound.PROD.equals(type) && duration.compareTo(Duration.ofSeconds(4)) >= 0) {
                // PROD ход длиннее 90 секунд - завершаем
                CompletableFuture.runAsync(() -> roundService.completeOldRound(round));
            }
        });
    }
}
