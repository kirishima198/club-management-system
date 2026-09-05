package com.example.club.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.club.dto.ActivityQuery;
import com.example.club.dto.ActivityVO;
import com.example.club.entity.Activity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ActivityMapper extends BaseMapper<Activity> {

    @Select("<script>" +
            "SELECT a.*, c.name AS club_name FROM activity a " +
            "JOIN club c ON a.club_id = c.id AND c.deleted = 0 " +
            "WHERE a.deleted = 0" +
            "<if test='q.keyword != null and q.keyword != \"\"'> AND a.title LIKE CONCAT('%', #{q.keyword}, '%')</if>" +
            "<if test='q.clubId != null'> AND a.club_id = #{q.clubId}</if>" +
            " ORDER BY a.start_time DESC" +
            "</script>")
    IPage<ActivityVO> selectPageVo(Page<?> page, @Param("q") ActivityQuery query);

    @Select("SELECT a.*, c.name AS club_name FROM activity a " +
            "JOIN club c ON a.club_id = c.id " +
            "WHERE a.id = #{id} AND a.deleted = 0")
    ActivityVO selectDetailById(@Param("id") Long id);

    @Select("SELECT a.*, c.name AS club_name FROM activity a " +
            "JOIN club c ON a.club_id = c.id AND c.deleted = 0 " +
            "WHERE a.deleted = 0 AND c.president_id = #{userId} ORDER BY a.start_time DESC")
    List<ActivityVO> selectByPresident(@Param("userId") Long userId);
}