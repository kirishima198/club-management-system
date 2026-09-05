package com.example.club.service.impl;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.common.BusinessException;
import com.example.club.context.LoginUser;
import com.example.club.dto.SignupVO;
import com.example.club.entity.Activity;
import com.example.club.entity.ActivitySignup;
import com.example.club.mapper.ActivityMapper;
import com.example.club.mapper.ActivitySignupMapper;
import com.example.club.service.ActivityService;
import com.example.club.service.ActivitySignupService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ActivitySignupServiceImpl extends ServiceImpl<ActivitySignupMapper, ActivitySignup>
        implements ActivitySignupService {

    @Resource
    private ActivityMapper activityMapper;

    @Resource
    private ActivityService activityService;

    @Override
    public void signup(Long activityId, Long userId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null || activity.getDeleted() == 1) {
            throw new BusinessException("活动不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        if (!now.isBefore(activity.getStartTime())) {
            throw new BusinessException("活动已开始或已结束，无法报名");
        }
        if (activity.getMaxParticipants() != null && activity.getMaxParticipants() > 0) {
            long signed = count(new LambdaQueryWrapper<ActivitySignup>()
                    .eq(ActivitySignup::getActivityId, activityId)
                    .eq(ActivitySignup::getStatus, 1));
            if (signed >= activity.getMaxParticipants()) {
                throw new BusinessException("报名人数已满");
            }
        }
        ActivitySignup existing = lambdaQuery()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getUserId, userId)
                .one();
        if (existing != null) {
            if (existing.getStatus() == 1) {
                throw new BusinessException("请勿重复报名");
            }
            LambdaUpdateWrapper<ActivitySignup> uw = new LambdaUpdateWrapper<>();
            uw.eq(ActivitySignup::getId, existing.getId())
                    .set(ActivitySignup::getStatus, 1)
                    .set(ActivitySignup::getCheckinStatus, 0)
                    .set(ActivitySignup::getCheckinTime, null)
                    .set(ActivitySignup::getCreateTime, now);
            update(uw);
        } else {
            ActivitySignup signup = new ActivitySignup();
            signup.setActivityId(activityId);
            signup.setUserId(userId);
            signup.setStatus(1);
            signup.setCheckinStatus(0);
            signup.setCreateTime(now);
            save(signup);
        }
    }

    @Override
    public void cancel(Long activityId, Long userId) {
        ActivitySignup existing = lambdaQuery()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getUserId, userId)
                .one();
        if (existing == null || existing.getStatus() != 1) {
            throw new BusinessException("你尚未报名该活动");
        }
        Activity activity = activityMapper.selectById(activityId);
        if (activity != null && !LocalDateTime.now().isBefore(activity.getStartTime())) {
            throw new BusinessException("活动已开始，无法取消报名");
        }
        LambdaUpdateWrapper<ActivitySignup> uw = new LambdaUpdateWrapper<>();
        uw.eq(ActivitySignup::getId, existing.getId()).set(ActivitySignup::getStatus, 0);
        update(uw);
    }

    @Override
    public IPage<SignupVO> mySignups(Long userId, long page, long size) {
        return baseMapper.selectMySignups(new Page<>(page, size), userId);
    }

    @Override
    public IPage<SignupVO> signups(Long activityId, long page, long size, LoginUser loginUser) {
        activityService.getManagedActivity(activityId, loginUser);
        return baseMapper.selectSignupPage(new Page<>(page, size), activityId);
    }

    @Override
    public void checkin(Long activityId, Long targetUserId, LoginUser loginUser) {
        activityService.getManagedActivity(activityId, loginUser);
        ActivitySignup signup = lambdaQuery()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getUserId, targetUserId)
                .one();
        if (signup == null || signup.getStatus() != 1) {
            throw new BusinessException("该用户未报名此活动");
        }
        if (signup.getCheckinStatus() == 1) {
            throw new BusinessException("该用户已签到");
        }
        LambdaUpdateWrapper<ActivitySignup> uw = new LambdaUpdateWrapper<>();
        uw.eq(ActivitySignup::getId, signup.getId())
                .set(ActivitySignup::getCheckinStatus, 1)
                .set(ActivitySignup::getCheckinTime, LocalDateTime.now());
        update(uw);
    }
}