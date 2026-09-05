package com.example.club.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

    Map<String, Object> overview();

    List<Map<String, Object>> clubMemberRanking();

    List<Map<String, Object>> activityByClub();
}