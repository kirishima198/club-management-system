package com.example.club.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ClubSaveDTO {

    @NotBlank(message = "社团名称不能为空")
    private String name;

    private String category;

    private String description;

    private String logo;
}