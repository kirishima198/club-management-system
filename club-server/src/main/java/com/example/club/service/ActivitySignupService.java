package com.example.club.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.club.context.LoginUser;
import com.example.club.dto.SignupVO;
import com.example.club.entity.ActivitySignup;

public interface ActivitySignupService extends IService<ActivitySignup> {

    void signup(Long activityId, Long userId);

    void cancel(Long activityId, Long userId);

    IPage<SignupVO> mySignups(Long userId, long page, long size);

    IPage<SignupVO> signups(Long activityId, long page, long size, LoginUser loginUser);

    void checkin(Long activityId, Long targetUserId, LoginUser loginUser);
}