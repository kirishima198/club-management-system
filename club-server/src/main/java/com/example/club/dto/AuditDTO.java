package com.example.club.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AuditDTO {

    @NotNull(message = "审核结果不能为空")
    private Boolean approve;

    private String reason;
}