package ru.abradox.statisticservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.abradox.client.statistic.StatusRound;
import ru.abradox.client.token.TypeToken;
import ru.abradox.platformapi.battle.ResultRound;
import ru.abradox.platformapi.battle.TypeRound;
import ru.abradox.platformapi.battle.event.FinishRound;
import ru.abradox.platformapi.battle.event.StartRound;
import ru.abradox.platformapi.battle.event.WantedRound;
import ru.abradox.statisticservice.model.entity.BotEntity;
import ru.abradox.statisticservice.model.entity.RoundEntity;
import ru.abradox.statisticservice.model.repository.BotRepository;
import ru.abradox.statisticservice.model.repository.RoundRepository;
import ru.abradox.statisticservice.service.RoundService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static ru.abradox.platformapi.battle.TypeRound.DEV;
import static ru.abradox.platformapi.battle.TypeRound.PROD;

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
            log.info("Запущен DEV раунд {} {} {}", round.getId(), bot1.getId(), bot2.getId());
        });
    }

    @Override
    @Transactional
    public void startProdRounds() {
        var freeProdRounds = roundRepository.findWaitProdRoundsWithNotPlayBots();
        if (freeProdRounds.isEmpty()) return;
        var futurePlayingBots = new HashSet<BotEntity>();
        freeProdRounds.forEach(round -> {
            var topBot = round.getTopBot();
            var downBot = round.getDownBot();
            if (!futurePlayingBots.contains(topBot) && !futurePlayingBots.contains(downBot)) {
                futurePlayingBots.add(topBot);
                futurePlayingBots.add(downBot);
                startRound(round, topBot, downBot);
            }
        });
    }

    private void startRound(RoundEntity round, BotEntity topBot, BotEntity downBot) {
        var isTopBotActive = topBot.getIsActive();
        var isDownBotActive = downBot.getIsActive();
        if (!isTopBotActive || !isDownBotActive) {
            // партию начинать нет смысла, так как один из ботов не подключен
            round.setStatus(StatusRound.FINISHED);
            var time = LocalDateTime.now();
            round.setBegin(time);
            round.setEnd(time);
            round.setResult(parseResult(isTopBotActive, isDownBotActive));
            roundRepository.save(round);
            log.info("Пропущен PROD раунд {} так как отсутствует одно из подключений", round.getId());
        } else {
            round.setStatus(StatusRound.PROGRESS);
            round.setBegin(LocalDateTime.now());
            topBot.setIsPlay(true);
            downBot.setIsPlay(true);
            roundRepository.save(round);
            rabbitTemplate.convertAndSend("start-round", "",
                    new StartRound(round.getId(), PROD, topBot.getToken(), downBot.getToken()));
            log.info("Запущен PROD раунд {} {} {}", round.getId(), topBot.getId(), downBot.getId());
        }
    }

    private ResultRound parseResult(boolean isTopBotActive, boolean isDownBotActive) {
        if (!isTopBotActive && !isDownBotActive) return ResultRound.DRAW;
        return isTopBotActive ? ResultRound.TOP : ResultRound.DOWN;
    }

    @Override
    public void startCompetition() {
        // проверяем, что все существующие PROD партии завершены
        // сканируем результаты PROD партий и перестраиваем рейтинг
        // удаляем все prod партии
        // создаём новые партии по новому рейтингу в статусе WAIT
    }

    @Override
    @Transactional
    public void finishRound(FinishRound finishRoundRequest) {
        var id = finishRoundRequest.getId();
        var result = finishRoundRequest.getResult();
        roundRepository.findByIdWithBots(id).ifPresent(round -> {
            log.info("Завершаю {} раунд", round.getId());
            var bot1 = round.getTopBot();
            var bot2 = round.getDownBot();
            bot1.setIsPlay(false);
            bot2.setIsPlay(false);
            botRepository.saveAll(List.of(bot1, bot2));

            var type = round.getType();
            if (TypeRound.DEV.equals(type)) {
                roundRepository.delete(round);
            } else if (TypeRound.PROD.equals(type)) {
                round.setStatus(StatusRound.FINISHED);
                round.setEnd(LocalDateTime.now());
                round.setResult(result);
                roundRepository.save(round);
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
            log.info("Обнаружена задержка в раунде {}", round.getId());
            var bot1 = round.getTopBot();
            var bot2 = round.getTopBot();
            rabbitTemplate.convertAndSend("wanted-round", "",
                    new WantedRound(round.getId(), round.getType(), bot1.getToken(), bot2.getToken()));
        });
    }
}
