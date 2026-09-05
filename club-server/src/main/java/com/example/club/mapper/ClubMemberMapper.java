package com.example.club.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.club.dto.MemberVO;
import com.example.club.entity.ClubMember;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ClubMemberMapper extends BaseMapper<ClubMember> {

    @Select("<script>" +
            "SELECT cm.*, u.username, u.nickname, u.avatar FROM club_member cm " +
            "JOIN sys_user u ON cm.user_id = u.id AND u.deleted = 0 " +
            "WHERE cm.club_id = #{clubId} AND cm.status = #{status}" +
            "<if test='keyword != null and keyword != \"\"'> AND (u.nickname LIKE CONCAT('%', #{keyword}, '%') OR u.username LIKE CONCAT('%', #{keyword}, '%'))</if>" +
            " ORDER BY cm.apply_time DESC" +
            "</script>")
    IPage<MemberVO> selectMemberPage(Page<?> page, @Param("clubId") Long clubId,
                                     @Param("status") Integer status, @Param("keyword") String keyword);
}