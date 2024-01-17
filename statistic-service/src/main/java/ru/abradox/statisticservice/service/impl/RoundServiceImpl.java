package ru.abradox.statisticservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abradox.client.statistic.StatusRound;
import ru.abradox.client.token.TypeToken;
import ru.abradox.platformapi.game.TypeRound;
import ru.abradox.platformapi.game.event.FinishRound;
import ru.abradox.platformapi.game.event.StartRound;
import ru.abradox.platformapi.game.event.WantedRound;
import ru.abradox.statisticservice.model.entity.BotEntity;
import ru.abradox.statisticservice.model.entity.RoundEntity;
import ru.abradox.statisticservice.model.repository.BotRepository;
import ru.abradox.statisticservice.model.repository.RoundRepository;
import ru.abradox.statisticservice.service.RoundService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.abradox.platformapi.game.TypeRound.DEV;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoundServiceImpl implements RoundService {

    private final BotRepository botRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RoundRepository roundRepository;

    @Override
    @Transactional
    public void startDevRounds() {

        var groupedFreeDevBots = botRepository.findAllByTypeAndIsActiveAndIsPlay(TypeToken.DEV, true, false)
                .stream()
                .collect(Collectors.groupingBy(BotEntity::getUserId))
                .values()
                .stream()
                .filter(botEntities -> botEntities.size() > 1)
                .toList();
        groupedFreeDevBots.forEach(freeDevBots -> {
            var bot1 = freeDevBots.get(0);
            var bot2 = freeDevBots.get(1);
            bot1.setIsPlay(true);
            bot2.setIsPlay(true);

            var round = RoundEntity.builder()
                    .topBot(bot1)
                    .downBot(bot2)
                    .type(TypeRound.DEV)
                    .status(StatusRound.PROGRESS)
                    .begin(LocalDateTime.now())
                    .build();

            round = roundRepository.save(round);
            rabbitTemplate.convertAndSend("start-round", "",
                    new StartRound(round.getId(), DEV, bot1.getToken(), bot2.getToken()));
        });
    }

    @Override
    @Transactional
    public void finishRound(FinishRound finishRoundRequest) {
        var id = finishRoundRequest.getId();
        roundRepository.findByIdWithBots(id).ifPresent(round -> {
            var type = round.getType();
            if (TypeRound.DEV.equals(type)) {
                var bot1 = round.getTopBot();
                var bot2 = round.getDownBot();
                bot1.setIsPlay(false);
                bot2.setIsPlay(false);
                botRepository.saveAll(List.of(bot1, bot2));
                roundRepository.delete(round);
            } else if (TypeRound.PROD.equals(type)) {
                // TODO завершение PROD раунда
            }
        });
    }

    @Override
    @Transactional
    public void validateRounds() {
        // ищем не завершённую партию status, которая продолжается более двух минут
        var beforeTime = LocalDateTime.now().minusMinutes(2);
        var oldRounds = roundRepository.findRoundsByStatusBeforeGivenTime(StatusRound.PROGRESS, beforeTime);
        oldRounds.forEach(round -> {
            var bot1 = round.getTopBot();
            var bot2 = round.getTopBot();
            rabbitTemplate.convertAndSend("wanted-round", "",
                    new WantedRound(round.getId(), round.getType(), bot1.getToken(), bot2.getToken()));
        });
    }
}
