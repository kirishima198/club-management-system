package com.example.club.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NoticeSaveDTO {

    @NotBlank(message = "公告标题不能为空")
    @Size(min = 1, max = 100, message = "公告标题长度为1-100个字符")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    private String content;

    private Long clubId;
}