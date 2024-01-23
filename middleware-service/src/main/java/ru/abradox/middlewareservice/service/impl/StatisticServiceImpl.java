package ru.abradox.middlewareservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.abradox.client.statistic.StatisticServiceClient;
import ru.abradox.middlewareservice.service.StatisticService;
import ru.abradox.platformapi.statistic.current.CompetitionInfo;
import ru.abradox.platformapi.statistic.history.HistoryInfo;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final StatisticServiceClient statisticClient;

    @Override
    // TODO добавить кеш
    public CompetitionInfo getCompetitionInfo() {
        return statisticClient.getCompetitionInfo();
    }

    @Override
    // TODO добавить кеш
    public List<HistoryInfo> getHistoryInfo() {
        return statisticClient.getHistoryInfo();
    }
}
