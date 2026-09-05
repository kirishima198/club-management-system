package com.example.club.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class ActivitySaveDTO {

    @NotNull(message = "所属社团不能为空")
    private Long clubId;

    @Size(min = 1, max = 100, message = "活动标题长度为1-100个字符")
    private String title;

    private String description;

    private String location;

    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private Integer maxParticipants;
}