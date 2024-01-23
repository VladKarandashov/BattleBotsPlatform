package ru.abradox.middlewareservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.abradox.common.response.GenericResponse;
import ru.abradox.middlewareservice.service.StatisticService;
import ru.abradox.platformapi.statistic.current.CompetitionInfo;
import ru.abradox.platformapi.statistic.history.HistoryInfo;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping("/api/v1/history")
    public GenericResponse<List<HistoryInfo>> getHistoryInfo() {
        return new GenericResponse<>(statisticService.getHistoryInfo());
    }

    @GetMapping("/api/v1/competition")
    public GenericResponse<CompetitionInfo> getCompetitionInfo() {
        return new GenericResponse<>(statisticService.getCompetitionInfo());
    }
}
