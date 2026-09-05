package com.example.club.dto;

import com.example.club.entity.Activity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ActivityVO extends Activity {

    private String clubName;

    private Long signupCount;

    /**
     * 活动状态(由起止时间推导): 0未开始 1进行中 2已结束
     */
    private Integer status;

    public static int computeStatus(java.time.LocalDateTime now, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        if (now.isBefore(start)) {
            return 0;
        }
        if (now.isAfter(end)) {
            return 2;
        }
        return 1;
    }
}