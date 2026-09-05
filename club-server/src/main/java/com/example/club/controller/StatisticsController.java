package com.example.club.controller;

import javax.annotation.Resource;

import com.example.club.common.Result;
import com.example.club.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return Result.ok(statisticsService.overview());
    }

    @GetMapping("/club-member-ranking")
    public Result<List<Map<String, Object>>> clubMemberRanking() {
        return Result.ok(statisticsService.clubMemberRanking());
    }

    @GetMapping("/activity-by-club")
    public Result<List<Map<String, Object>>> activityByClub() {
        return Result.ok(statisticsService.activityByClub());
    }
}