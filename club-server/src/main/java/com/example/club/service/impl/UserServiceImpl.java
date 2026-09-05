package com.example.club.service.impl;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.common.BusinessException;
import com.example.club.context.LoginUser;
import com.example.club.dto.ChangePasswordDTO;
import com.example.club.dto.LoginDTO;
import com.example.club.dto.LoginVO;
import com.example.club.dto.ProfileDTO;
import com.example.club.dto.RegisterDTO;
import com.example.club.dto.UserQuery;
import com.example.club.dto.UserSaveDTO;
import com.example.club.entity.User;
import com.example.club.mapper.UserMapper;
import com.example.club.service.UserService;
import com.example.club.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private JwtUtil jwtUtil;

    private static String md5(String raw) {
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = lambdaQuery().eq(User::getUsername, dto.getUsername()).one();
        if (user == null || !md5(dto.getPassword()).equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }
        LoginUser loginUser = new LoginUser();
        loginUser.setId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setRole(user.getRole());
        LoginVO vo = new LoginVO();
        vo.setToken(jwtUtil.createToken(loginUser));
        user.setPassword(null);
        vo.setUserInfo(user);
        return vo;
    }

    @Override
    public void register(RegisterDTO dto) {
        if (count(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())) > 0) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(md5(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole("student");
        user.setStatus(1);
        save(user);
    }

    @Override
    public void changePassword(ChangePasswordDTO dto, Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!md5(dto.getOldPassword()).equals(user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(md5(dto.getNewPassword()));
        updateById(user);
    }

    @Override
    public void updateProfile(ProfileDTO dto, Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (dto.getNickname() != null && !dto.getNickname().trim().isEmpty()) {
            user.setNickname(dto.getNickname().trim());
        }
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        updateById(user);
    }

    @Override
    public IPage<User> page(UserQuery query) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        String keyword = query.getKeyword();
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(User::getUsername, kw).or().like(User::getNickname, kw));
        }
        wrapper.eq(query.getRole() != null && !query.getRole().isEmpty(), User::getRole, query.getRole());
        wrapper.eq(query.getStatus() != null, User::getStatus, query.getStatus());
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = page(new Page<>(query.getPage(), query.getSize()), wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        return result;
    }

    @Override
    public void addUser(UserSaveDTO dto) {
        if (count(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())) > 0) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        String password = dto.getPassword() == null || dto.getPassword().isEmpty() ? "123456" : dto.getPassword();
        user.setPassword(md5(password));
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole() == null || dto.getRole().isEmpty() ? "student" : dto.getRole());
        user.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        save(user);
    }

    @Override
    public void updateUser(Long id, UserSaveDTO dto) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        Long dup = count(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()).ne(User::getId, id));
        if (dup > 0) {
            throw new BusinessException("用户名已存在");
        }
        user.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(md5(dto.getPassword()));
        }
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        if (dto.getRole() != null && !dto.getRole().isEmpty()) {
            user.setRole(dto.getRole());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        updateById(user);
    }

    @Override
    public void deleteUser(Long id, Long currentUserId) {
        if (id.equals(currentUserId)) {
            throw new BusinessException("不能删除自己");
        }
        removeById(id);
    }

    @Override
    public void resetPassword(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(md5("123456"));
        updateById(user);
    }

    @Override
    public void updateStatus(Long id, Integer status, Long currentUserId) {
        if (id.equals(currentUserId)) {
            throw new BusinessException("不能禁用自己的账号");
        }
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        updateById(user);
    }
}