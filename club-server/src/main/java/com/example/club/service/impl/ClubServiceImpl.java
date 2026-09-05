package com.example.club.service.impl;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.common.BusinessException;
import com.example.club.context.LoginUser;
import com.example.club.dto.AuditDTO;
import com.example.club.dto.ClubQuery;
import com.example.club.dto.ClubSaveDTO;
import com.example.club.dto.ClubVO;
import com.example.club.entity.Club;
import com.example.club.entity.ClubMember;
import com.example.club.entity.User;
import com.example.club.mapper.ClubMapper;
import com.example.club.mapper.ClubMemberMapper;
import com.example.club.mapper.UserMapper;
import com.example.club.service.ClubService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClubServiceImpl extends ServiceImpl<ClubMapper, Club> implements ClubService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ClubMemberMapper clubMemberMapper;

    @Override
    public void apply(ClubSaveDTO dto, Long userId) {
        if (count(new LambdaQueryWrapper<Club>().eq(Club::getName, dto.getName())) > 0) {
            throw new BusinessException("社团名称已存在");
        }
        Club club = new Club();
        club.setName(dto.getName());
        club.setCategory(dto.getCategory());
        club.setDescription(dto.getDescription());
        club.setLogo(dto.getLogo());
        club.setPresidentId(userId);
        club.setStatus(0);
        club.setMemberCount(0);
        save(club);
    }

    @Override
    public IPage<ClubVO> page(ClubQuery query, LoginUser loginUser) {
        if (!"admin".equals(loginUser.getRole())) {
            // 非管理员只能查看已过审社团
            query.setStatus(1);
        }
        return baseMapper.selectPageVo(new Page<>(query.getPage(), query.getSize()), query);
    }

    @Override
    public ClubVO detail(Long id) {
        ClubVO vo = baseMapper.selectDetailById(id);
        if (vo == null) {
            throw new BusinessException("社团不存在");
        }
        return vo;
    }

    @Override
    public List<Club> myCreated(Long userId) {
        return lambdaQuery().eq(Club::getPresidentId, userId).orderByDesc(Club::getCreateTime).list();
    }

    @Override
    public void update(Long id, ClubSaveDTO dto, LoginUser loginUser) {
        Club club = getById(id);
        if (club == null) {
            throw new BusinessException("社团不存在");
        }
        if (!"admin".equals(loginUser.getRole()) && !club.getPresidentId().equals(loginUser.getId())) {
            throw new BusinessException("无权限修改该社团");
        }
        Long dup = count(new LambdaQueryWrapper<Club>().eq(Club::getName, dto.getName()).ne(Club::getId, id));
        if (dup > 0) {
            throw new BusinessException("社团名称已存在");
        }
        club.setName(dto.getName());
        club.setCategory(dto.getCategory());
        club.setDescription(dto.getDescription());
        club.setLogo(dto.getLogo());
        updateById(club);
    }

    @Override
    public void delete(Long id) {
        removeById(id);
        clubMemberMapper.delete(new LambdaQueryWrapper<ClubMember>().eq(ClubMember::getClubId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id, AuditDTO dto) {
        Club club = getById(id);
        if (club == null) {
            throw new BusinessException("社团不存在");
        }
        if (club.getStatus() != 0) {
            throw new BusinessException("该申请已处理，请勿重复审核");
        }
        if (Boolean.TRUE.equals(dto.getApprove())) {
            club.setStatus(1);
            updateById(club);
            // 社长角色升级: student -> leader
            User president = userMapper.selectById(club.getPresidentId());
            if (president != null && "student".equals(president.getRole())) {
                president.setRole("leader");
                userMapper.updateById(president);
            }
            // 社长自动成为成员并计入成员数
            Long exists = clubMemberMapper.selectCount(new LambdaQueryWrapper<ClubMember>()
                    .eq(ClubMember::getClubId, id)
                    .eq(ClubMember::getUserId, club.getPresidentId()));
            if (exists == 0) {
                ClubMember member = new ClubMember();
                member.setClubId(id);
                member.setUserId(club.getPresidentId());
                member.setStatus(1);
                member.setHandleTime(LocalDateTime.now());
                clubMemberMapper.insert(member);
                club.setMemberCount(club.getMemberCount() + 1);
                updateById(club);
            }
        } else {
            club.setStatus(2);
            club.setRejectReason(dto.getReason());
            updateById(club);
        }
    }
}