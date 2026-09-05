package com.example.club.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.club.dto.ChangePasswordDTO;
import com.example.club.dto.LoginDTO;
import com.example.club.dto.LoginVO;
import com.example.club.dto.ProfileDTO;
import com.example.club.dto.RegisterDTO;
import com.example.club.dto.UserQuery;
import com.example.club.dto.UserSaveDTO;
import com.example.club.entity.User;

public interface UserService extends IService<User> {

    LoginVO login(LoginDTO dto);

    void register(RegisterDTO dto);

    void changePassword(ChangePasswordDTO dto, Long userId);

    void updateProfile(ProfileDTO dto, Long userId);

    IPage<User> page(UserQuery query);

    void addUser(UserSaveDTO dto);

    void updateUser(Long id, UserSaveDTO dto);

    void deleteUser(Long id, Long currentUserId);

    void resetPassword(Long id);

    void updateStatus(Long id, Integer status, Long currentUserId);
}