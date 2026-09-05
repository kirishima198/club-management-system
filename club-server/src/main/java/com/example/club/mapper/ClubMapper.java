package com.example.club.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.club.dto.ClubQuery;
import com.example.club.dto.ClubVO;
import com.example.club.entity.Club;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface ClubMapper extends BaseMapper<Club> {

    @Select("<script>" +
            "SELECT c.*, u.nickname AS president_name FROM club c " +
            "LEFT JOIN sys_user u ON c.president_id = u.id AND u.deleted = 0 " +
            "WHERE c.deleted = 0" +
            "<if test='q.keyword != null and q.keyword != \"\"'> AND c.name LIKE CONCAT('%', #{q.keyword}, '%')</if>" +
            "<if test='q.category != null and q.category != \"\"'> AND c.category = #{q.category}</if>" +
            "<if test='q.status != null'> AND c.status = #{q.status}</if>" +
            " ORDER BY c.create_time DESC" +
            "</script>")
    IPage<ClubVO> selectPageVo(Page<?> page, @Param("q") ClubQuery query);

    @Select("SELECT c.*, u.nickname AS president_name FROM club c " +
            "LEFT JOIN sys_user u ON c.president_id = u.id AND u.deleted = 0 " +
            "WHERE c.id = #{id} AND c.deleted = 0")
    ClubVO selectDetailById(@Param("id") Long id);

    @Select("SELECT c.*, u.nickname AS president_name, cm.create_time AS join_time FROM club c " +
            "JOIN club_member cm ON cm.club_id = c.id AND cm.user_id = #{userId} AND cm.status = 1 " +
            "LEFT JOIN sys_user u ON c.president_id = u.id " +
            "WHERE c.deleted = 0 AND c.status = 1 ORDER BY cm.create_time DESC")
    List<ClubVO> selectMyJoined(@Param("userId") Long userId);

    @Select("SELECT name, member_count AS `value` FROM club WHERE deleted = 0 AND status = 1 ORDER BY member_count DESC LIMIT 10")
    List<Map<String, Object>> selectMemberRanking();

    @Select("SELECT c.name AS name, COUNT(a.id) AS `value` FROM club c " +
            "LEFT JOIN activity a ON a.club_id = c.id AND a.deleted = 0 " +
            "WHERE c.deleted = 0 AND c.status = 1 GROUP BY c.id, c.name ORDER BY `value` DESC")
    List<Map<String, Object>> selectActivityCount();
}