package ru.abradox.statisticservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abradox.client.statistic.StatusRound;
import ru.abradox.client.statistic.TypeRound;
import ru.abradox.client.token.TypeToken;
import ru.abradox.platformapi.game.StartRound;
import ru.abradox.statisticservice.db.entity.BotEntity;
import ru.abradox.statisticservice.db.entity.RoundEntity;
import ru.abradox.statisticservice.db.repository.BotRepository;
import ru.abradox.statisticservice.db.repository.RoundRepository;
import ru.abradox.statisticservice.service.RoundService;

import java.time.LocalDateTime;
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
}
