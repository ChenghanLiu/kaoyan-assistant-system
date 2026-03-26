# 系统架构说明

## 1. 总体架构

系统采用前后端分离架构：

- 前端：Vue 3 + Element Plus，负责页面渲染、交互控制、令牌存储和接口调用
- 后端：Spring Boot 3，负责鉴权、业务处理、数据访问和统一响应
- 数据层：MySQL 8，使用 Flyway 管理数据库版本迁移
- 存储层：资料文件保存到本地 `uploads/` 目录

## 2. 分层结构

后端目录结构遵循分层设计：

### `api`

- 负责 HTTP 接口暴露
- 接收请求参数、调用应用服务、返回统一 `ApiResponse`
- 按角色和业务域划分控制器，如 `api.admin`、`api.student`、`api.auth`

### `application`

- 负责具体业务编排
- 处理 DTO 转换、校验补充、聚合查询、日志记录等
- 如 `AuthService`、`StudyPlanService`、`MaterialService`、`AdminStatsService`

### `domain`

- 存放实体类和枚举
- 实体与数据库表一一对应
- 例如 `SysUser`、`StudyPlan`、`Material`、`SystemNotice`

### `infrastructure`

- 存放仓储接口和基础设施实现
- 如 JPA Repository、本地文件存储服务
- 对外提供数据持久化与文件读写能力

## 3. 认证与权限

- 用户登录成功后由后端生成 JWT
- 前端将 JWT 存入本地，并在后续请求中放入 `Authorization: Bearer <token>`
- Spring Security 通过过滤器解析令牌并恢复当前登录用户
- 控制器通过 `@PreAuthorize` 控制角色访问范围

## 4. 典型业务流

### 登录流程

1. 前端提交用户名和密码到 `/api/auth/login`
2. 后端使用 Spring Security 完成认证
3. 登录成功后签发 JWT 并返回用户信息
4. 同时写入操作日志，便于后台展示

### 资料审核流程

1. 学生上传资料到 `/api/student/materials`
2. 后端保存文件并写入 `material` 表，状态为 `PENDING`
3. 管理员在资料审核页调用 `/api/admin/materials/{id}/review`
4. 审核结果写回资料表，并生成操作日志

### 统计分析流程

1. 管理员访问 `/api/admin/stats/overview`
2. 后端从用户、计划、考试、资料、帖子、评论等现有表聚合数据
3. 统一返回统计结果供管理员仪表盘展示

## 5. 设计原则

- 保持目录结构与现有项目一致
- 不引入复杂中间件或额外服务
- 后端字段命名统一使用英文
- 统计分析基于真实业务数据计算
- 系统管理模块保持轻量、可演示、易维护
