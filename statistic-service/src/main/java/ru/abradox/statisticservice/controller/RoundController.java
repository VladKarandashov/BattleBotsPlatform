package ru.abradox.statisticservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.abradox.platformapi.statistic.CompetitionInfo;
import ru.abradox.statisticservice.service.RoundService;

@RestController
@RequiredArgsConstructor
public class RoundController {

    private final RoundService roundService;

    @GetMapping("/api/v1/round")
    public CompetitionInfo getCompetitionInfo() {
        return roundService.getCompetitionInfo();
    }
}
