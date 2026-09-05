package com.example.club.controller;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.club.annotation.RequireRole;
import com.example.club.common.Result;
import com.example.club.context.UserContext;
import com.example.club.dto.UserQuery;
import com.example.club.dto.UserSaveDTO;
import com.example.club.entity.User;
import com.example.club.service.UserService;
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

@RestController
@RequestMapping("/api/users")
@RequireRole("admin")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping
    public Result<IPage<User>> page(UserQuery query) {
        return Result.ok(userService.page(query));
    }

    @PostMapping
    public Result<Void> add(@Valid @RequestBody UserSaveDTO dto) {
        userService.addUser(dto);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserSaveDTO dto) {
        userService.updateUser(id, dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id, UserContext.getUserId());
        return Result.ok();
    }

    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status, UserContext.getUserId());
        return Result.ok();
    }
}