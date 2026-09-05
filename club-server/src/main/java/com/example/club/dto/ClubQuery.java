package com.example.club.dto;

import lombok.Data;

@Data
public class ClubQuery {

    private Long page = 1L;

    private Long size = 10L;

    private String keyword;

    private String category;

    private Integer status;
}