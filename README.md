# 考研助手系统

本项目是一个面向毕业设计答辩与演示的考研辅助平台，采用前后端分离架构，覆盖学生端与管理员端两类角色。当前版本已完成登录鉴权、学习计划、资料共享与审核、模拟考试、讨论区、院校与专业信息、招生简章、报录比、政策资讯、统计分析、系统公告、系统配置、操作日志等核心功能，达到“可答辩 + 可演示 + 功能完整”的毕业设计实现目标。

## 项目简介

- 项目名称：考研助手系统
- 项目定位：为考研学生提供学习计划管理、资料共享、模拟考试、交流互动与院校信息查询服务，同时支持管理员进行审核、内容维护与统计分析
- 适用场景：毕业设计展示、课程项目演示、基础教学样例
- 角色说明：
  - `STUDENT`：学生用户，负责使用学习、资料、考试、讨论等业务功能
  - `ADMIN`：管理员，负责审核、维护、统计和轻量系统管理

## 技术栈

- 后端：Spring Boot 3、Spring Security、JWT、Spring Data JPA、Flyway、MySQL 8
- 前端：Vue 3、Vite、Element Plus、Pinia、Vue Router、Axios
- 部署与运行：Docker Compose、Maven、npm
- 文件存储：本地 `uploads/` 目录

## 功能模块说明

### 学生端

- 登录注册与 JWT 鉴权
- 学习计划、学习任务、进度记录、提醒
- 资料列表、资料上传、我的上传、资料下载
- 模拟考试、在线答题、考试记录、错题本
- 讨论区帖子、评论互动
- 院校列表、院校详情、专业、招生简章、报录比、政策资讯
- 首页公告展示与个人信息展示

### 管理员端

- 用户管理
- 资料审核
- 招生简章、报录比、政策资讯管理
- 讨论区内容管理
- 统计分析看板
- 系统公告管理
- 系统配置管理
- 操作日志查询

## 系统架构说明

### 前后端分离

- 前端负责页面展示、交互和令牌存储
- 后端负责认证鉴权、业务处理、数据持久化和统一返回
- 前后端通过 `/api/**` REST 接口通信

### 分层结构

- `api`：控制器层，负责路由、参数接收、权限入口
- `application`：应用服务层，负责业务编排和 DTO 转换
- `domain`：实体、枚举等核心领域模型
- `infrastructure`：仓储、文件存储等基础设施实现
- `common`：安全、异常、通用响应、配置

详细说明见 [系统架构说明](./docs/system-architecture.md)。

## 项目结构

```text
kaoyan-assistant-system
├── backend
│   ├── src/main/java/com/kaoyan/assistant
│   │   ├── api
│   │   ├── application
│   │   ├── common
│   │   ├── domain
│   │   └── infrastructure
│   ├── src/main/resources
│   │   ├── application.yml
│   │   └── db/migration
│   └── src/test
├── frontend
│   ├── src/api
│   ├── src/layout
│   ├── src/router
│   ├── src/stores
│   └── src/views
├── docs
├── uploads
└── docker-compose.yml
```

## 启动方式

### 1. Docker 启动数据库

在项目根目录执行：

```bash
docker compose up -d mysql
```

默认数据库配置：

- Host：`localhost`
- Port：`3307`
- Database：`kaoyan_assistant_system`
- Username：`kaoyan_user`
- Password：`kaoyan_pass`

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端默认地址：`http://localhost:8080`

可选环境变量：

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `STORAGE_LOCATION`
- `FRONTEND_ORIGIN`

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认地址：`http://localhost:5173`

## 默认账号

- 管理员：`admin / 123456`
- 学生：`student / 123456`

## 统一返回结构

后端统一返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

## API 示例

### 1. 登录

`POST /api/auth/login`

```json
{
  "username": "admin",
  "password": "123456"
}
```

### 2. 获取管理员统计总览

`GET /api/admin/stats/overview`

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "userStats": {
      "totalUsers": 2,
      "studentCount": 1,
      "adminCount": 1
    },
    "studyPlanStats": {
      "totalPlans": 2,
      "completedTaskCount": 2,
      "pendingTaskCount": 3,
      "averageCompletionRate": 40.0
    },
    "examStats": {
      "participationCount": 1,
      "averageScore": 82.0,
      "highestScore": 82,
      "lowestScore": 82
    },
    "materialStats": {
      "totalMaterials": 3,
      "approvedCount": 2,
      "pendingCount": 1
    },
    "contentStats": {
      "postCount": 3,
      "commentCount": 3
    }
  }
}
```

### 3. 获取系统公告

`GET /api/notices`

### 4. 修改系统配置

`PATCH /api/admin/configs/site_name`

```json
{
  "configValue": "考研助手系统答辩版",
  "configDescription": "系统名称"
}
```

### 5. 查询操作日志

`GET /api/admin/logs`

## 文档索引

- [数据库设计文档](./docs/database-design.md)
- [系统架构说明](./docs/system-architecture.md)
- [接口设计文档](./docs/api-design.md)
- [需求文档](./docs/requirements.md)
- [任务拆分](./docs/task-split.md)

## 当前版本说明

当前版本严格控制复杂度，不引入 AI、推荐系统等扩展能力，重点完成毕业设计评分常见考察项：

- 核心业务链路完整
- 管理端功能可演示
- 统计数据真实可查
- 文档结构完整
- 数据库脚本可直接初始化
