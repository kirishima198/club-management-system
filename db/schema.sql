-- ============================================================
-- 高校学生社团管理系统 数据库脚本
-- MySQL 8.x，字符集 utf8mb4
-- ============================================================
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS club_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE club_system;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL COMMENT '登录名/学号',
    password    VARCHAR(64)  NOT NULL COMMENT '密码(MD5)',
    nickname    VARCHAR(50)  DEFAULT NULL COMMENT '姓名',
    avatar      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    email       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    role        VARCHAR(20)  NOT NULL DEFAULT 'student' COMMENT '角色: admin/leader/student',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0否 1是',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- ----------------------------
-- 2. 社团表
-- ----------------------------
DROP TABLE IF EXISTS club;
CREATE TABLE club (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '社团ID',
    name         VARCHAR(100) NOT NULL COMMENT '社团名称',
    category     VARCHAR(50)  DEFAULT NULL COMMENT '分类: 文艺/体育/学术/科技/公益/其他',
    description  TEXT         COMMENT '社团简介',
    logo         VARCHAR(255) DEFAULT NULL COMMENT 'Logo URL',
    president_id BIGINT       NOT NULL COMMENT '社长用户ID',
    member_count INT          NOT NULL DEFAULT 0 COMMENT '成员数量(已审核通过)',
    status       TINYINT      NOT NULL DEFAULT 0 COMMENT '状态: 0待审核 1已通过 2已驳回',
    reject_reason VARCHAR(255) DEFAULT NULL COMMENT '驳回原因',
    deleted      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name),
    KEY idx_president (president_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '社团表';

-- ----------------------------
-- 3. 社团成员表
-- ----------------------------
DROP TABLE IF EXISTS club_member;
CREATE TABLE club_member (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    club_id     BIGINT      NOT NULL COMMENT '社团ID',
    user_id     BIGINT      NOT NULL COMMENT '用户ID',
    status      TINYINT     NOT NULL DEFAULT 0 COMMENT '状态: 0申请中 1已加入 2已拒绝 3已退出',
    apply_reason VARCHAR(255) DEFAULT NULL COMMENT '申请留言',
    apply_time  DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    handle_time DATETIME    DEFAULT NULL COMMENT '审批时间',
    create_time DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_club_user (club_id, user_id),
    KEY idx_user (user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '社团成员表';

-- ----------------------------
-- 4. 活动表
-- ----------------------------
DROP TABLE IF EXISTS activity;
CREATE TABLE activity (
    id                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '活动ID',
    club_id           BIGINT       NOT NULL COMMENT '所属社团ID',
    title             VARCHAR(100) NOT NULL COMMENT '活动标题',
    description       TEXT         COMMENT '活动详情',
    location          VARCHAR(100) DEFAULT NULL COMMENT '活动地点',
    start_time        DATETIME     NOT NULL COMMENT '开始时间',
    end_time          DATETIME     NOT NULL COMMENT '结束时间',
    max_participants  INT          NOT NULL DEFAULT 0 COMMENT '人数上限, 0=不限',
    create_by         BIGINT       DEFAULT NULL COMMENT '发布人ID',
    deleted           TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_club (club_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '活动表';

-- ----------------------------
-- 5. 活动报名表
-- ----------------------------
DROP TABLE IF EXISTS activity_signup;
CREATE TABLE activity_signup (
    id             BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    activity_id    BIGINT   NOT NULL COMMENT '活动ID',
    user_id        BIGINT   NOT NULL COMMENT '用户ID',
    status         TINYINT  NOT NULL DEFAULT 1 COMMENT '状态: 1已报名 0已取消',
    checkin_status TINYINT  NOT NULL DEFAULT 0 COMMENT '签到: 0未签到 1已签到',
    checkin_time   DATETIME DEFAULT NULL COMMENT '签到时间',
    create_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    update_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_activity_user (activity_id, user_id),
    KEY idx_user (user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '活动报名表';

-- ----------------------------
-- 6. 公告表
-- ----------------------------
DROP TABLE IF EXISTS notice;
CREATE TABLE notice (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    title        VARCHAR(100) NOT NULL COMMENT '公告标题',
    content      TEXT         NOT NULL COMMENT '公告内容',
    club_id      BIGINT       DEFAULT NULL COMMENT '社团ID, NULL=全局公告',
    publisher_id BIGINT       NOT NULL COMMENT '发布人ID',
    deleted      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_club (club_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '公告表';

SET FOREIGN_KEY_CHECKS = 1;
