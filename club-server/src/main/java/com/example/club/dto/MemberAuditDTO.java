package com.example.club.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MemberAuditDTO {

    @NotNull(message = "审批结果不能为空")
    private Boolean approve;
}