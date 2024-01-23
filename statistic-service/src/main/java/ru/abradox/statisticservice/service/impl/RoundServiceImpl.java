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
import ru.abradox.platformapi.statistic.RoundResult;
import ru.abradox.statisticservice.model.entity.BotEntity;
import ru.abradox.statisticservice.model.entity.HistoryEntity;
import ru.abradox.statisticservice.model.entity.RoundEntity;
import ru.abradox.statisticservice.model.repository.BotRepository;
import ru.abradox.statisticservice.model.repository.HistoryRepository;
import ru.abradox.statisticservice.model.repository.RoundRepository;
import ru.abradox.statisticservice.service.RoundService;

import java.time.LocalDateTime;
import java.util.*;
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
    private final HistoryRepository historyRepository;

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
    @Transactional
    public void startCompetition() {
        // проверяем, что все существующие PROD партии завершены
        var areAllProdRoundsComplete = roundRepository.areAllRoundsWithGivenTypeHaveGivenStatus(PROD, StatusRound.FINISHED);
        if (!areAllProdRoundsComplete) return;
        // сканируем результаты PROD партий
        var resultMap = processResultByDownBots();
        var orderedBots = botRepository.findAllByTypeAndPositionIsNotNullOrderByPosition(TypeToken.PROD);
        // сохраняем рейтинг в историю
        saveHistory(orderedBots, resultMap);
        // перестраиваем рейтинг
        var resultBotRating = getBotRating(orderedBots, resultMap);
        // добавляем в рейтинг новых ботов
        resultBotRating.addAll(botRepository.findAllByTypeAndPositionIsNull(TypeToken.PROD));
        // проставляем новые position
        for (int i = 0; i < resultBotRating.size(); i++) {
            resultBotRating.get(i).setPosition(i + 1);
        }
        botRepository.saveAll(resultBotRating);
        // удаляем все prod партии
        roundRepository.deleteAllByType(PROD);
        // создаём новые партии по новому рейтингу в статусе WAIT
        makeRounds();
    }

    private List<BotEntity> getBotRating(List<BotEntity> orderedBots, Map<Integer, RoundResult> resultMap) {
        var resultBotRating = new LinkedList<BotEntity>();
        orderedBots.forEach(bot -> {
            var botResult = resultMap.get(bot.getId());
            var isBotDeferTop = botResult != null && botResult.getIsDownBotWin();
            // если данный бот победил своего TOP-a - вставляем его перед ним - иначе в конец
            if (isBotDeferTop) {
                var topBotId = botResult.getTopBotId();
                var topBotIndex = botIndexById(resultBotRating, topBotId);
                resultBotRating.add(topBotIndex, bot);
            } else {
                resultBotRating.add(bot);
            }
        });
        return resultBotRating;
    }

    private Map<Integer, RoundResult> processResultByDownBots() {
        var roundList = roundRepository.findRoundsByStatusBeforeGivenTime(PROD);
        return roundList.stream()
                .collect(Collectors.groupingBy(round -> round.getDownBot().getId()))
                .entrySet()
                .stream()
                .map(roundsGroup -> {
                    var rounds = roundsGroup.getValue();
                    var downBotId = roundsGroup.getKey();
                    var topBotId = rounds.get(0).getTopBot().getId();
                    var downBotWinCount = rounds.stream().filter(round -> round.getResult().equals(ResultRound.DOWN)).count();
                    var topBotWinCount = rounds.stream().filter(round -> round.getResult().equals(ResultRound.TOP)).count();
                    var isDownBotWin = (downBotWinCount > topBotWinCount);
                    return new RoundResult(downBotId, topBotId, downBotWinCount, topBotWinCount, isDownBotWin);
                }).collect(Collectors.toMap(RoundResult::getDownBotId, el -> el));

    }

    private void saveHistory(List<BotEntity> botRatingBeforeCompetition, Map<Integer, RoundResult> resultMap) {
        if (botRatingBeforeCompetition.isEmpty()) return;
        var orderedBotIdList = botRatingBeforeCompetition.stream().map(BotEntity::getId).toList();
        var resultRoundList = new ArrayList<>(resultMap.values());
        var historyElement = new HistoryEntity(orderedBotIdList, resultRoundList);
        historyRepository.save(historyElement);
    }

    private Integer botIndexById(List<BotEntity> bots, Integer id) {
        for (int i = 0; i < bots.size(); i++) {
            var bot = bots.get(i);
            if (Objects.equals(bot.getId(), id)) return i;
        }
        return -1;
    }

    private void makeRounds() {
        var competitionBots = botRepository.findAllByTypeAndPositionIsNotNullOrderByPosition(TypeToken.PROD);
        List<RoundEntity> roundList = new ArrayList<>();
        for (int i = 1; i < competitionBots.size(); i++) {
            var topBot = competitionBots.get(i - 1);
            var downBot = competitionBots.get(i);
            for (int j = 0; j < 5; j++) {
                roundList.add(RoundEntity.builder()
                        .topBot(topBot)
                        .downBot(downBot)
                        .type(TypeRound.PROD)
                        .status(StatusRound.WAIT)
                        .build());
            }
        }
        roundList = roundRepository.saveAll(roundList);
        log.info("Успешно создано новое соревнование, включающее {} раундов", roundList.size());
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
            var bot2 = round.getDownBot();
            rabbitTemplate.convertAndSend("wanted-round", "",
                    new WantedRound(round.getId(), round.getType(), bot1.getToken(), bot2.getToken()));
        });
    }
}
