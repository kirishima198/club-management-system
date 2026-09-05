# 高校学生社团管理系统

基于 **Spring Boot + Vue 3** 的高校学生社团管理系统，覆盖管理员、社团社长、学生三种角色的完整业务流程：社团申请与审核、成员管理、活动发布与报名签到、公告通知、数据统计等。

## 技术栈

| 层次 | 技术 |
|------|------|
| 后端 | Spring Boot 2.7.18 · MyBatis-Plus 3.5.3 · JWT (jjwt) · MySQL 8 · Maven |
| 前端 | Vue 3 · Vite 4 · Element Plus · Pinia · Vue Router 4 · Axios · ECharts 5 |
| 部署 | 单 jar 一键部署（前端静态资源内嵌后端，SPA 路由回退） |

## 功能清单

### 管理员（admin）
- 用户管理：新增 / 编辑 / 禁用 / 重置密码
- 社团审核：通过 / 驳回（含驳回原因），解散社团
- 活动管理：查看、删除全部活动
- 公告通知：发布 / 编辑 / 删除
- 数据统计：用户 / 社团 / 活动 / 公告数量、社团成员排行、活动分布（ECharts）

### 社团社长（leader）
- 社团信息：编辑本社团简介
- 成员管理：查看成员、审批加入申请（通过 / 拒绝）、移除成员
- 活动管理：发布 / 编辑 / 删除本社团活动
- 活动签到：在报名名单中对成员一键签到

### 学生（student）
- 社团广场：浏览 / 搜索已过审社团，查看详情，申请加入
- 申请建社：提交建社申请，查看审核状态与驳回原因
- 我的社团：我创建的（管理入口）/ 我加入的（退出）
- 活动报名：报名 / 取消报名，我的报名列表与签到状态
- 个人中心：修改资料、修改密码

## 快速开始

### 环境要求

- JDK 8+
- MySQL 8.x（运行中，账号 root / 密码与下述配置一致）
- Node.js 16+（仅开发模式需要）

### 1. 初始化数据库

使用 MySQL 客户端依次执行：

```sql
source db/schema.sql
source db/seed.sql
```

数据库连接配置见 `club-server/src/main/resources/application.yml`，默认：

```
url: jdbc:mysql://localhost:3306/club_system?...
username: root
password: 123456
```

如本地密码不同，请修改后重新打包。

### 2. 一键启动（生产模式，推荐）

双击项目根目录的 `start.bat`，或命令行执行：

```bat
start.bat
```

脚本会启动后端服务（内嵌前端页面）并自动打开浏览器，访问 <http://localhost:8080>。

### 3. 开发模式（前后端分离热更新）

```bat
:: 后端（端口 8080）
cd club-server
mvnw.cmd spring-boot:run

:: 前端（端口 5173，已配置 /api 代理到 8080）
cd club-web
npm install
npm run dev
```

访问 <http://localhost:5173>。

## 种子账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 系统管理员 |
| 社长 | leader1 | 123456 | 张伟（篮球社社长） |
| 社长 | leader2 | 123456 | 李娜（计算机协会社长） |
| 学生 | student1 | 123456 | 王小明 |
| 学生 | student2 | 123456 | 赵雪 |
| 学生 | student3 | 123456 | 陈晨 |
| 学生 | student4 | 123456 | 刘洋 |

## 项目结构

```
piing
├── db/                      # 数据库脚本（schema.sql / seed.sql）
├── club-server/             # 后端 Spring Boot 工程
│   └── src/main/java/com/example/club
│       ├── controller/      # 7 个 REST Controller
│       ├── service/         # 业务接口与实现（含事务）
│       ├── mapper/          # MyBatis-Plus Mapper
│       ├── entity/          # 6 张表实体（逻辑删除）
│       ├── dto/             # 请求 / 响应对象（VO/DTO）
│       ├── interceptor/     # JWT 认证 + @RequireRole 角色校验
│       └── config/          # Web / SPA 回退配置
├── club-web/                # 前端 Vue 3 工程
│   └── src
│       ├── api/             # Axios 接口封装
│       ├── views/           # 页面（club / activity / user / notice / profile）
│       ├── router/          # 路由与角色守卫
│       ├── stores/          # Pinia 状态
│       └── utils/           # request 拦截器（token / 统一错误处理）
└── start.bat                # 一键启动脚本
```

## 接口约定

- 统一响应：`{ "code": 200, "msg": "操作成功", "data": ... }`，非 200 视为业务失败
- 认证：登录后携带 `Authorization: Bearer <token>`，401 未登录 / 403 无权限
- 关键写操作（审核、报名计数等）使用数据库事务保证一致性