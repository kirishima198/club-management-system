package com.example.club.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.club.dto.NoticeVO;
import com.example.club.entity.Notice;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface NoticeMapper extends BaseMapper<Notice> {

    @Select("<script>" +
            "SELECT n.*, c.name AS club_name, u.nickname AS publisher_name FROM notice n " +
            "LEFT JOIN club c ON n.club_id = c.id " +
            "LEFT JOIN sys_user u ON n.publisher_id = u.id " +
            "WHERE n.deleted = 0" +
            "<if test='clubId != null'> AND n.club_id = #{clubId}</if>" +
            " ORDER BY n.create_time DESC" +
            "</script>")
    IPage<NoticeVO> selectPageVo(Page<?> page, @Param("clubId") Long clubId);
}