package com.example.club.service.impl;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.common.BusinessException;
import com.example.club.context.LoginUser;
import com.example.club.dto.ActivityQuery;
import com.example.club.dto.ActivitySaveDTO;
import com.example.club.dto.ActivityVO;
import com.example.club.entity.Activity;
import com.example.club.entity.Club;
import com.example.club.mapper.ActivityMapper;
import com.example.club.mapper.ActivitySignupMapper;
import com.example.club.mapper.ClubMapper;
import com.example.club.service.ActivityService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Resource
    private ClubMapper clubMapper;

    @Resource
    private ActivitySignupMapper activitySignupMapper;

    @Override
    public void publish(ActivitySaveDTO dto, LoginUser loginUser) {
        Club club = clubMapper.selectById(dto.getClubId());
        if (club == null || club.getDeleted() == 1) {
            throw new BusinessException("社团不存在");
        }
        if (club.getStatus() != 1) {
            throw new BusinessException("社团未通过审核，无法发布活动");
        }
        if (!club.getPresidentId().equals(loginUser.getId())) {
            throw new BusinessException("只有社团社长可以发布活动");
        }
        checkTime(dto.getStartTime(), dto.getEndTime());
        Activity activity = new Activity();
        BeanUtils.copyProperties(dto, activity);
        activity.setId(null);
        activity.setCreateBy(loginUser.getId());
        save(activity);
    }

    @Override
    public IPage<ActivityVO> page(ActivityQuery query) {
        IPage<ActivityVO> result = baseMapper.selectPageVo(new Page<>(query.getPage(), query.getSize()), query);
        fillExtra(result.getRecords());
        return result;
    }

    @Override
    public ActivityVO detail(Long id) {
        ActivityVO vo = baseMapper.selectDetailById(id);
        if (vo == null) {
            throw new BusinessException("活动不存在");
        }
        fillExtra(Collections.singletonList(vo));
        return vo;
    }

    @Override
    public void update(Long id, ActivitySaveDTO dto, LoginUser loginUser) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        checkManagePermission(activity, loginUser);
        checkTime(dto.getStartTime(), dto.getEndTime());
        activity.setTitle(dto.getTitle());
        activity.setDescription(dto.getDescription());
        activity.setLocation(dto.getLocation());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setMaxParticipants(dto.getMaxParticipants());
        updateById(activity);
    }

    @Override
    public void delete(Long id, LoginUser loginUser) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        checkManagePermission(activity, loginUser);
        removeById(id);
    }

    @Override
    public List<ActivityVO> myPublished(Long userId) {
        List<ActivityVO> list = baseMapper.selectByPresident(userId);
        fillExtra(list);
        return list;
    }

    @Override
    public Activity getManagedActivity(Long activityId, LoginUser loginUser) {
        Activity activity = getById(activityId);
        if (activity == null || activity.getDeleted() == 1) {
            throw new BusinessException("活动不存在");
        }
        checkManagePermission(activity, loginUser);
        return activity;
    }

    private void checkManagePermission(Activity activity, LoginUser loginUser) {
        if ("admin".equals(loginUser.getRole())) {
            return;
        }
        Club club = clubMapper.selectById(activity.getClubId());
        if (club == null || !club.getPresidentId().equals(loginUser.getId())) {
            throw new BusinessException("无权限操作该活动");
        }
    }

    private void checkTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (endTime.isBefore(startTime)) {
            throw new BusinessException("结束时间不能早于开始时间");
        }
    }

    private void fillExtra(List<ActivityVO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        List<Long> ids = list.stream().map(ActivityVO::getId).collect(Collectors.toList());
        Map<Long, Long> countMap = activitySignupMapper.countByActivityIds(ids).stream()
                .collect(Collectors.toMap(
                        m -> ((Number) m.get("activity_id")).longValue(),
                        m -> ((Number) m.get("cnt")).longValue()));
        LocalDateTime now = LocalDateTime.now();
        for (ActivityVO vo : list) {
            vo.setSignupCount(countMap.getOrDefault(vo.getId(), 0L));
            vo.setStatus(ActivityVO.computeStatus(now, vo.getStartTime(), vo.getEndTime()));
        }
    }
}