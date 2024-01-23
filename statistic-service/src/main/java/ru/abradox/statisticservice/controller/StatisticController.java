package ru.abradox.statisticservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.abradox.platformapi.statistic.current.CompetitionInfo;
import ru.abradox.platformapi.statistic.history.HistoryInfo;
import ru.abradox.statisticservice.service.RoundService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatisticController {

    private final RoundService roundService;

    @GetMapping("/api/v1/history")
    public List<HistoryInfo> getHistoryInfo() {
        return roundService.getHistoryInfo();
    }

    @GetMapping("/api/v1/competition")
    public CompetitionInfo getCompetitionInfo() {
        return roundService.getCompetitionInfo();
    }
}
