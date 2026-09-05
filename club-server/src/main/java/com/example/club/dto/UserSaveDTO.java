package com.example.club.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserSaveDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phone;

    private String role;

    private Integer status;
}