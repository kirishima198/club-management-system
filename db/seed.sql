-- ============================================================
-- 初始种子数据
-- 账号: admin/admin123  leader1/123456  leader2/123456  student1~4/123456
-- ============================================================
USE club_system;

-- 用户 (密码为 MD5 值)
INSERT INTO sys_user (id, username, password, nickname, email, phone, role, status) VALUES
(1, 'admin',    '0192023a7bbd73250516f069df18b500', '系统管理员', 'admin@club.com',  '13800000001', 'admin',   1),
(2, 'leader1',  'e10adc3949ba59abbe56e057f20f883e', '张伟',       'zhangwei@club.com', '13800000002', 'leader',  1),
(3, 'leader2',  'e10adc3949ba59abbe56e057f20f883e', '李娜',       'lina@club.com',   '13800000003', 'leader',  1),
(4, 'student1', 'e10adc3949ba59abbe56e057f20f883e', '王小明',     NULL,              '13800000004', 'student', 1),
(5, 'student2', 'e10adc3949ba59abbe56e057f20f883e', '赵雪',       NULL,              '13800000005', 'student', 1),
(6, 'student3', 'e10adc3949ba59abbe56e057f20f883e', '陈晨',       NULL,              '13800000006', 'student', 1),
(7, 'student4', 'e10adc3949ba59abbe56e057f20f883e', '刘洋',       NULL,              '13800000007', 'student', 1);

-- 社团
INSERT INTO club (id, name, category, description, president_id, member_count, status) VALUES
(1, '篮球社',     '体育', '以球会友，定期组织训练与校内比赛，欢迎热爱篮球的同学加入。', 2, 3, 1),
(2, '计算机协会', '科技', '学习编程、算法与新技术，组织技术讲座与开发实践。',             3, 2, 1),
(3, '摄影社',     '文艺', '用镜头记录校园生活，交流摄影技巧，组织外拍活动。',             6, 0, 0);

-- 社团成员
INSERT INTO club_member (club_id, user_id, status, apply_reason, apply_time, handle_time) VALUES
(1, 2, 1, NULL,            '2026-09-01 10:00:00', '2026-09-01 10:00:00'),
(1, 4, 1, '喜欢打篮球',     '2026-09-01 11:00:00', '2026-09-01 12:00:00'),
(1, 5, 1, '想锻炼身体',     '2026-09-01 12:30:00', '2026-09-01 13:00:00'),
(2, 3, 1, NULL,            '2026-09-01 10:00:00', '2026-09-01 10:00:00'),
(2, 7, 1, '对编程感兴趣',   '2026-09-02 09:00:00', '2026-09-02 10:00:00'),
(2, 4, 0, '想学习编程',     '2026-09-04 20:00:00', NULL);

-- 活动
INSERT INTO activity (id, club_id, title, description, location, start_time, end_time, max_participants, create_by) VALUES
(1, 1, '迎新篮球赛',   '新生友谊赛，欢迎各社团同学组队参加，赛后有聚餐。', '学校体育馆',   '2026-09-15 14:00:00', '2026-09-15 17:00:00', 50, 2),
(2, 2, '编程入门讲座', '面向零基础同学的 Python 入门讲座，请自带电脑。',    '实验楼A301',  '2026-09-18 19:00:00', '2026-09-18 21:00:00', 30, 3),
(3, 1, '三分大赛',     '社团内部三分球大赛，冠军有神秘奖品。',             '篮球场1号场', '2026-08-20 15:00:00', '2026-08-20 18:00:00', 0,  2);

-- 活动报名
INSERT INTO activity_signup (activity_id, user_id, status, checkin_status, checkin_time, create_time) VALUES
(1, 5, 1, 0, NULL, '2026-09-03 10:00:00'),
(2, 7, 1, 0, NULL, '2026-09-03 11:00:00'),
(2, 4, 1, 0, NULL, '2026-09-03 14:00:00'),
(3, 4, 1, 1, '2026-08-20 15:10:00', '2026-08-15 10:00:00'),
(3, 5, 1, 1, '2026-08-20 15:12:00', '2026-08-15 11:00:00');

-- 公告
INSERT INTO notice (title, content, club_id, publisher_id) VALUES
('关于社团注册审核流程的通知', '各同学请注意：申请创建社团需填写完整的社团简介与分类，管理员将在3个工作日内完成审核，审核结果可在"个人中心-我的申请"中查看。', NULL, 1),
('本周训练安排', '本周三下午4点在篮球场1号场进行常规训练，请各位成员准时参加，训练后安排迎新篮球赛报名事宜。', 1, 2);
