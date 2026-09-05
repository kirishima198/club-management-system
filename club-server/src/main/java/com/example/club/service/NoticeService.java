package com.example.club.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.club.context.LoginUser;
import com.example.club.dto.NoticeSaveDTO;
import com.example.club.dto.NoticeVO;
import com.example.club.entity.Notice;

public interface NoticeService extends IService<Notice> {

    void publish(NoticeSaveDTO dto, LoginUser loginUser);

    IPage<NoticeVO> page(long page, long size, Long clubId);

    void update(Long id, NoticeSaveDTO dto, LoginUser loginUser);

    void delete(Long id, LoginUser loginUser);
}