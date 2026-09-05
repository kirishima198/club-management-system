package com.example.club.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.club.context.LoginUser;
import com.example.club.dto.ActivityQuery;
import com.example.club.dto.ActivitySaveDTO;
import com.example.club.dto.ActivityVO;
import com.example.club.entity.Activity;

import java.util.List;

public interface ActivityService extends IService<Activity> {

    void publish(ActivitySaveDTO dto, LoginUser loginUser);

    IPage<ActivityVO> page(ActivityQuery query);

    ActivityVO detail(Long id);

    void update(Long id, ActivitySaveDTO dto, LoginUser loginUser);

    void delete(Long id, LoginUser loginUser);

    List<ActivityVO> myPublished(Long userId);

    /**
     * 校验活动存在且当前用户有管理权限(社长本人或管理员)，返回活动实体
     */
    Activity getManagedActivity(Long activityId, LoginUser loginUser);
}