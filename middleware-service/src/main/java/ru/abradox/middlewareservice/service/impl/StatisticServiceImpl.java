package ru.abradox.middlewareservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.abradox.client.statistic.StatisticServiceClient;
import ru.abradox.middlewareservice.service.StatisticService;
import ru.abradox.platformapi.statistic.current.CompetitionInfo;
import ru.abradox.platformapi.statistic.history.HistoryInfo;

import java.util.List;

import static ru.abradox.middlewareservice.config.CacheConfig.COMPETITION_INFO_CACHE;
import static ru.abradox.middlewareservice.config.CacheConfig.HISTORY_INFO_CACHE;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final StatisticServiceClient statisticClient;

    @Override
    @Cacheable(COMPETITION_INFO_CACHE)
    public CompetitionInfo getCompetitionInfo() {
        return statisticClient.getCompetitionInfo();
    }

    @Override
    @Cacheable(HISTORY_INFO_CACHE)
    public List<HistoryInfo> getHistoryInfo() {
        return statisticClient.getHistoryInfo();
    }
}
