package com.example.club.dto;

import com.example.club.entity.ActivitySignup;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SignupVO extends ActivitySignup {

    private String username;

    private String nickname;

    private String title;

    private String clubName;
}