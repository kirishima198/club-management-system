package com.example.club.dto;

import lombok.Data;

@Data
public class UserQuery {

    private Long page = 1L;

    private Long size = 10L;

    private String keyword;

    private String role;

    private Integer status;
}