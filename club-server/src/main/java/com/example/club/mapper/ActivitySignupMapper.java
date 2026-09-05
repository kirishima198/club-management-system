package com.example.club.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.club.dto.SignupVO;
import com.example.club.entity.ActivitySignup;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface ActivitySignupMapper extends BaseMapper<ActivitySignup> {

    @Select("SELECT s.*, u.username, u.nickname FROM activity_signup s " +
            "JOIN sys_user u ON s.user_id = u.id AND u.deleted = 0 " +
            "WHERE s.activity_id = #{activityId} AND s.status = 1 ORDER BY s.create_time ASC")
    IPage<SignupVO> selectSignupPage(Page<?> page, @Param("activityId") Long activityId);

    @Select("SELECT s.*, a.title, c.name AS club_name FROM activity_signup s " +
            "JOIN activity a ON s.activity_id = a.id AND a.deleted = 0 " +
            "JOIN club c ON a.club_id = c.id AND c.deleted = 0 " +
            "WHERE s.user_id = #{userId} AND s.status = 1 ORDER BY s.create_time DESC")
    IPage<SignupVO> selectMySignups(Page<?> page, @Param("userId") Long userId);

    @Select("<script>SELECT activity_id, COUNT(*) AS cnt FROM activity_signup WHERE status = 1 AND activity_id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach> " +
            "GROUP BY activity_id</script>")
    List<Map<String, Object>> countByActivityIds(@Param("ids") List<Long> ids);
}