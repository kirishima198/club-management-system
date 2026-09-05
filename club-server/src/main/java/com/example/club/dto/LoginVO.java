package com.example.club.dto;

import com.example.club.entity.User;
import lombok.Data;

@Data
public class LoginVO {

    private String token;

    private User userInfo;
}