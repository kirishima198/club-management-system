package com.example.club.dto;

import com.example.club.entity.Notice;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NoticeVO extends Notice {

    private String clubName;

    private String publisherName;
}