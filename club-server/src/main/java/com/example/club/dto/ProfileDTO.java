package com.example.club.dto;

import lombok.Data;

/**
 * 用户自主修改个人资料
 */
@Data
public class ProfileDTO {

    private String nickname;

    private String email;

    private String phone;
}