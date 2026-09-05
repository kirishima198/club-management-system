package com.example.club.service.impl;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.club.entity.Club;
import com.example.club.mapper.ActivityMapper;
import com.example.club.mapper.ClubMapper;
import com.example.club.mapper.NoticeMapper;
import com.example.club.mapper.UserMapper;
import com.example.club.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ClubMapper clubMapper;

    @Resource
    private ActivityMapper activityMapper;

    @Resource
    private NoticeMapper noticeMapper;

    @Override
    public Map<String, Object> overview() {
        Map<String, Object> map = new HashMap<>();
        map.put("userCount", userMapper.selectCount(null));
        map.put("clubCount", clubMapper.selectCount(new LambdaQueryWrapper<Club>().eq(Club::getStatus, 1)));
        map.put("activityCount", activityMapper.selectCount(null));
        map.put("noticeCount", noticeMapper.selectCount(null));
        return map;
    }

    @Override
    public List<Map<String, Object>> clubMemberRanking() {
        return clubMapper.selectMemberRanking();
    }

    @Override
    public List<Map<String, Object>> activityByClub() {
        return clubMapper.selectActivityCount();
    }
}