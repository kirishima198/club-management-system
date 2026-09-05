package com.example.club.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.club.context.LoginUser;
import com.example.club.dto.ClubVO;
import com.example.club.dto.JoinDTO;
import com.example.club.dto.MemberAuditDTO;
import com.example.club.dto.MemberVO;
import com.example.club.entity.ClubMember;

import java.util.List;

public interface ClubMemberService extends IService<ClubMember> {

    void apply(Long clubId, JoinDTO dto, Long userId);

    List<ClubVO> myJoined(Long userId);

    IPage<MemberVO> members(Long clubId, long page, long size, String keyword, LoginUser loginUser);

    IPage<MemberVO> pending(Long clubId, long page, long size, LoginUser loginUser);

    void auditMember(Long clubId, Long memberId, MemberAuditDTO dto, LoginUser loginUser);

    void removeMember(Long clubId, Long memberId, LoginUser loginUser);

    void quit(Long clubId, Long userId);
}