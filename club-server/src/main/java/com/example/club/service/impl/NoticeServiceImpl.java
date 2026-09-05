package com.example.club.service.impl;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.club.common.BusinessException;
import com.example.club.context.LoginUser;
import com.example.club.dto.NoticeSaveDTO;
import com.example.club.dto.NoticeVO;
import com.example.club.entity.Club;
import com.example.club.entity.Notice;
import com.example.club.mapper.ClubMapper;
import com.example.club.mapper.NoticeMapper;
import com.example.club.service.NoticeService;
import org.springframework.stereotype.Service;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Resource
    private ClubMapper clubMapper;

    @Override
    public void publish(NoticeSaveDTO dto, LoginUser loginUser) {
        Notice notice = new Notice();
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setPublisherId(loginUser.getId());
        if ("admin".equals(loginUser.getRole())) {
            notice.setClubId(null);
        } else if ("leader".equals(loginUser.getRole())) {
            if (dto.getClubId() == null) {
                throw new BusinessException("请选择要发布公告的社团");
            }
            Club club = clubMapper.selectById(dto.getClubId());
            if (club == null || club.getDeleted() == 1) {
                throw new BusinessException("社团不存在");
            }
            if (!club.getPresidentId().equals(loginUser.getId())) {
                throw new BusinessException("只能在自己管理的社团发布公告");
            }
            notice.setClubId(dto.getClubId());
        } else {
            throw new BusinessException("无权限发布公告");
        }
        save(notice);
    }

    @Override
    public IPage<NoticeVO> page(long page, long size, Long clubId) {
        return baseMapper.selectPageVo(new Page<>(page, size), clubId);
    }

    @Override
    public void update(Long id, NoticeSaveDTO dto, LoginUser loginUser) {
        Notice notice = getById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        if (!"admin".equals(loginUser.getRole()) && !notice.getPublisherId().equals(loginUser.getId())) {
            throw new BusinessException("无权限操作该公告");
        }
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        updateById(notice);
    }

    @Override
    public void delete(Long id, LoginUser loginUser) {
        Notice notice = getById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        if (!"admin".equals(loginUser.getRole()) && !notice.getPublisherId().equals(loginUser.getId())) {
            throw new BusinessException("无权限操作该公告");
        }
        removeById(id);
    }
}