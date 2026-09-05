package com.example.club.controller;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.club.annotation.RequireRole;
import com.example.club.common.Result;
import com.example.club.context.UserContext;
import com.example.club.dto.ActivityQuery;
import com.example.club.dto.ActivitySaveDTO;
import com.example.club.dto.ActivityVO;
import com.example.club.dto.SignupVO;
import com.example.club.service.ActivityService;
import com.example.club.service.ActivitySignupService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    @Resource
    private ActivityService activityService;

    @Resource
    private ActivitySignupService activitySignupService;

    @PostMapping
    @RequireRole("leader")
    public Result<Void> publish(@Valid @RequestBody ActivitySaveDTO dto) {
        activityService.publish(dto, UserContext.get());
        return Result.ok();
    }

    @GetMapping
    public Result<IPage<ActivityVO>> page(ActivityQuery query) {
        return Result.ok(activityService.page(query));
    }

    @GetMapping("/my/published")
    @RequireRole("leader")
    public Result<List<ActivityVO>> myPublished() {
        return Result.ok(activityService.myPublished(UserContext.getUserId()));
    }

    @GetMapping("/my/signups")
    @RequireRole("student")
    public Result<IPage<SignupVO>> mySignups(@RequestParam(defaultValue = "1") long page,
                                             @RequestParam(defaultValue = "10") long size) {
        return Result.ok(activitySignupService.mySignups(UserContext.getUserId(), page, size));
    }

    @GetMapping("/{id}")
    public Result<ActivityVO> detail(@PathVariable Long id) {
        return Result.ok(activityService.detail(id));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ActivitySaveDTO dto) {
        activityService.update(id, dto, UserContext.get());
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        activityService.delete(id, UserContext.get());
        return Result.ok();
    }

    @PutMapping("/{id}/signup")
    @RequireRole("student")
    public Result<Void> signup(@PathVariable Long id) {
        activitySignupService.signup(id, UserContext.getUserId());
        return Result.ok();
    }

    @DeleteMapping("/{id}/signup")
    @RequireRole("student")
    public Result<Void> cancelSignup(@PathVariable Long id) {
        activitySignupService.cancel(id, UserContext.getUserId());
        return Result.ok();
    }

    @GetMapping("/{id}/signups")
    public Result<IPage<SignupVO>> signups(@PathVariable Long id,
                                           @RequestParam(defaultValue = "1") long page,
                                           @RequestParam(defaultValue = "10") long size) {
        return Result.ok(activitySignupService.signups(id, page, size, UserContext.get()));
    }

    @PutMapping("/{id}/signups/{userId}/checkin")
    public Result<Void> checkin(@PathVariable Long id, @PathVariable Long userId) {
        activitySignupService.checkin(id, userId, UserContext.get());
        return Result.ok();
    }
}