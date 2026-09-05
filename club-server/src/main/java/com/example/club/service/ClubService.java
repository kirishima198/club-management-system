package com.example.club.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.club.context.LoginUser;
import com.example.club.dto.AuditDTO;
import com.example.club.dto.ClubQuery;
import com.example.club.dto.ClubSaveDTO;
import com.example.club.dto.ClubVO;
import com.example.club.entity.Club;

import java.util.List;

public interface ClubService extends IService<Club> {

    void apply(ClubSaveDTO dto, Long userId);

    IPage<ClubVO> page(ClubQuery query, LoginUser loginUser);

    ClubVO detail(Long id);

    List<Club> myCreated(Long userId);

    void update(Long id, ClubSaveDTO dto, LoginUser loginUser);

    void delete(Long id);

    void audit(Long id, AuditDTO dto);
}