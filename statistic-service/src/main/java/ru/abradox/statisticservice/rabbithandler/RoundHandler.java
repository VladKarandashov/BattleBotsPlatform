package ru.abradox.statisticservice.rabbithandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.abradox.platformapi.game.FinishRound;
import ru.abradox.statisticservice.service.RoundService;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoundHandler {

    private final RoundService roundService;

    @RabbitListener(queues = "finish-round")
    public void finishRound(FinishRound finishRound) {
        log.info("Завершаю партию {}", finishRound.getId());
        roundService.finishRound(finishRound);
    }
}
