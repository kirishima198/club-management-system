package com.example.club.controller;

import javax.annotation.Resource;

import com.example.club.common.Result;
import com.example.club.context.UserContext;
import com.example.club.dto.ChangePasswordDTO;
import com.example.club.dto.LoginDTO;
import com.example.club.dto.LoginVO;
import com.example.club.dto.ProfileDTO;
import com.example.club.dto.RegisterDTO;
import com.example.club.entity.User;
import com.example.club.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.ok(userService.login(dto));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.ok();
    }

    @GetMapping("/me")
    public Result<User> me() {
        User user = userService.getById(UserContext.getUserId());
        if (user != null) {
            user.setPassword(null);
        }
        return Result.ok(user);
    }

    @PutMapping("/password")
    public Result<Void> password(@Valid @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(dto, UserContext.getUserId());
        return Result.ok();
    }

    @PutMapping("/profile")
    public Result<Void> profile(@RequestBody ProfileDTO dto) {
        userService.updateProfile(dto, UserContext.getUserId());
        return Result.ok();
    }
}