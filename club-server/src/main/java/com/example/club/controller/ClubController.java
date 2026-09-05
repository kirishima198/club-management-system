package com.example.club.controller;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.club.annotation.RequireRole;
import com.example.club.common.Result;
import com.example.club.context.UserContext;
import com.example.club.dto.AuditDTO;
import com.example.club.dto.ClubQuery;
import com.example.club.dto.ClubSaveDTO;
import com.example.club.dto.ClubVO;
import com.example.club.entity.Club;
import com.example.club.service.ClubService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    @Resource
    private ClubService clubService;

    @PostMapping
    @RequireRole("student")
    public Result<Void> apply(@Valid @RequestBody ClubSaveDTO dto) {
        clubService.apply(dto, UserContext.getUserId());
        return Result.ok();
    }

    @GetMapping
    public Result<IPage<ClubVO>> page(ClubQuery query) {
        return Result.ok(clubService.page(query, UserContext.get()));
    }

    @GetMapping("/my/created")
    
    public Result<List<Club>> myCreated() {
        return Result.ok(clubService.myCreated(UserContext.getUserId()));
    }

    @GetMapping("/{id}")
    public Result<ClubVO> detail(@PathVariable Long id) {
        return Result.ok(clubService.detail(id));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ClubSaveDTO dto) {
        clubService.update(id, dto, UserContext.get());
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequireRole("admin")
    public Result<Void> delete(@PathVariable Long id) {
        clubService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/audit")
    @RequireRole("admin")
    public Result<Void> audit(@PathVariable Long id, @Valid @RequestBody AuditDTO dto) {
        clubService.audit(id, dto);
        return Result.ok();
    }
}