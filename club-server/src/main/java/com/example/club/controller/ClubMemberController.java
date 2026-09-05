package com.example.club.controller;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.club.annotation.RequireRole;
import com.example.club.common.Result;
import com.example.club.context.UserContext;
import com.example.club.dto.ClubVO;
import com.example.club.dto.JoinDTO;
import com.example.club.dto.MemberAuditDTO;
import com.example.club.dto.MemberVO;
import com.example.club.service.ClubMemberService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
public class ClubMemberController {

    @Resource
    private ClubMemberService clubMemberService;

    @PostMapping("/{clubId}/apply")
    @RequireRole("student")
    public Result<Void> apply(@PathVariable Long clubId, @RequestBody(required = false) JoinDTO dto) {
        clubMemberService.apply(clubId, dto == null ? new JoinDTO() : dto, UserContext.getUserId());
        return Result.ok();
    }

    @GetMapping("/my/joined")
    public Result<List<ClubVO>> myJoined() {
        return Result.ok(clubMemberService.myJoined(UserContext.getUserId()));
    }

    @GetMapping("/{clubId}/members")
    public Result<IPage<MemberVO>> members(@PathVariable Long clubId,
                                           @RequestParam(defaultValue = "1") long page,
                                           @RequestParam(defaultValue = "10") long size,
                                           @RequestParam(required = false) String keyword) {
        return Result.ok(clubMemberService.members(clubId, page, size, keyword, UserContext.get()));
    }

    @GetMapping("/{clubId}/members/pending")
    public Result<IPage<MemberVO>> pending(@PathVariable Long clubId,
                                           @RequestParam(defaultValue = "1") long page,
                                           @RequestParam(defaultValue = "10") long size) {
        return Result.ok(clubMemberService.pending(clubId, page, size, UserContext.get()));
    }

    @PutMapping("/{clubId}/members/{memberId}/audit")
    public Result<Void> auditMember(@PathVariable Long clubId, @PathVariable Long memberId,
                                    @RequestBody MemberAuditDTO dto) {
        clubMemberService.auditMember(clubId, memberId, dto, UserContext.get());
        return Result.ok();
    }

    @DeleteMapping("/{clubId}/members/{memberId}")
    public Result<Void> removeMember(@PathVariable Long clubId, @PathVariable Long memberId) {
        clubMemberService.removeMember(clubId, memberId, UserContext.get());
        return Result.ok();
    }

    @DeleteMapping("/{clubId}/quit")
    public Result<Void> quit(@PathVariable Long clubId) {
        clubMemberService.quit(clubId, UserContext.getUserId());
        return Result.ok();
    }
}