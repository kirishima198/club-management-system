package com.example.club.service.impl;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.common.BusinessException;
import com.example.club.context.LoginUser;
import com.example.club.dto.ClubVO;
import com.example.club.dto.JoinDTO;
import com.example.club.dto.MemberAuditDTO;
import com.example.club.dto.MemberVO;
import com.example.club.entity.Club;
import com.example.club.entity.ClubMember;
import com.example.club.mapper.ClubMapper;
import com.example.club.mapper.ClubMemberMapper;
import com.example.club.service.ClubMemberService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClubMemberServiceImpl extends ServiceImpl<ClubMemberMapper, ClubMember> implements ClubMemberService {

    @Resource
    private ClubMapper clubMapper;

    private Club checkClubAdmin(Long clubId, LoginUser loginUser) {
        Club club = clubMapper.selectById(clubId);
        if (club == null || club.getDeleted() == 1) {
            throw new BusinessException("社团不存在");
        }
        if (!"admin".equals(loginUser.getRole()) && !club.getPresidentId().equals(loginUser.getId())) {
            throw new BusinessException("无权限管理该社团成员");
        }
        return club;
    }

    @Override
    public void apply(Long clubId, JoinDTO dto, Long userId) {
        Club club = clubMapper.selectById(clubId);
        if (club == null || club.getDeleted() == 1) {
            throw new BusinessException("社团不存在");
        }
        if (club.getStatus() != 1) {
            throw new BusinessException("该社团暂未开放加入");
        }
        LocalDateTime now = LocalDateTime.now();
        ClubMember existing = lambdaQuery()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getUserId, userId)
                .one();
        if (existing != null) {
            if (existing.getStatus() == 0) {
                throw new BusinessException("已提交申请，等待社长审核");
            }
            if (existing.getStatus() == 1) {
                throw new BusinessException("你已是该社团成员");
            }
            // 曾被拒绝或已退出，重新提交申请
            LambdaUpdateWrapper<ClubMember> uw = new LambdaUpdateWrapper<>();
            uw.eq(ClubMember::getId, existing.getId())
                    .set(ClubMember::getStatus, 0)
                    .set(ClubMember::getApplyReason, dto == null ? null : dto.getApplyReason())
                    .set(ClubMember::getApplyTime, now)
                    .set(ClubMember::getHandleTime, null);
            update(uw);
        } else {
            ClubMember member = new ClubMember();
            member.setClubId(clubId);
            member.setUserId(userId);
            member.setStatus(0);
            member.setApplyReason(dto == null ? null : dto.getApplyReason());
            member.setApplyTime(now);
            save(member);
        }
    }

    @Override
    public List<ClubVO> myJoined(Long userId) {
        return clubMapper.selectMyJoined(userId);
    }

    @Override
    public IPage<MemberVO> members(Long clubId, long page, long size, String keyword, LoginUser loginUser) {
        checkClubAdmin(clubId, loginUser);
        return baseMapper.selectMemberPage(new Page<>(page, size), clubId, 1, keyword);
    }

    @Override
    public IPage<MemberVO> pending(Long clubId, long page, long size, LoginUser loginUser) {
        checkClubAdmin(clubId, loginUser);
        return baseMapper.selectMemberPage(new Page<>(page, size), clubId, 0, null);
    }

    @Override
    public void auditMember(Long clubId, Long memberId, MemberAuditDTO dto, LoginUser loginUser) {
        Club club = checkClubAdmin(clubId, loginUser);
        ClubMember member = getById(memberId);
        if (member == null || !member.getClubId().equals(clubId)) {
            throw new BusinessException("申请记录不存在");
        }
        if (member.getStatus() != 0) {
            throw new BusinessException("该申请已处理");
        }
        if (Boolean.TRUE.equals(dto.getApprove())) {
            member.setStatus(1);
            member.setHandleTime(LocalDateTime.now());
            updateById(member);
            club.setMemberCount(club.getMemberCount() + 1);
            clubMapper.updateById(club);
        } else {
            member.setStatus(2);
            member.setHandleTime(LocalDateTime.now());
            updateById(member);
        }
    }

    @Override
    public void removeMember(Long clubId, Long memberId, LoginUser loginUser) {
        Club club = checkClubAdmin(clubId, loginUser);
        ClubMember member = getById(memberId);
        if (member == null || !member.getClubId().equals(clubId)) {
            throw new BusinessException("成员不存在");
        }
        if (member.getStatus() != 1) {
            throw new BusinessException("该记录不是正式成员");
        }
        removeById(memberId);
        club.setMemberCount(Math.max(0, club.getMemberCount() - 1));
        clubMapper.updateById(club);
    }

    @Override
    public void quit(Long clubId, Long userId) {
        Club club = clubMapper.selectById(clubId);
        if (club == null || club.getDeleted() == 1) {
            throw new BusinessException("社团不存在");
        }
        ClubMember member = lambdaQuery()
                .eq(ClubMember::getClubId, clubId)
                .eq(ClubMember::getUserId, userId)
                .one();
        if (member == null || member.getStatus() != 1) {
            throw new BusinessException("你还不是该社团成员");
        }
        removeById(member.getId());
        club.setMemberCount(Math.max(0, club.getMemberCount() - 1));
        clubMapper.updateById(club);
    }
}