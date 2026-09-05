package com.example.club.controller;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.club.common.Result;
import com.example.club.context.UserContext;
import com.example.club.dto.NoticeSaveDTO;
import com.example.club.dto.NoticeVO;
import com.example.club.service.NoticeService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    @PostMapping
    public Result<Void> publish(@Valid @RequestBody NoticeSaveDTO dto) {
        noticeService.publish(dto, UserContext.get());
        return Result.ok();
    }

    @GetMapping
    public Result<IPage<NoticeVO>> page(@RequestParam(defaultValue = "1") long page,
                                        @RequestParam(defaultValue = "10") long size,
                                        @RequestParam(required = false) Long clubId) {
        return Result.ok(noticeService.page(page, size, clubId));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody NoticeSaveDTO dto) {
        noticeService.update(id, dto, UserContext.get());
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.delete(id, UserContext.get());
        return Result.ok();
    }
}