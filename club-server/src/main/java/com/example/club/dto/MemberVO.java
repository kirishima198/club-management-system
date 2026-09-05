package com.example.club.dto;

import com.example.club.entity.ClubMember;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MemberVO extends ClubMember {

    private String username;

    private String nickname;

    private String avatar;
}