# 接口设计文档

## 1. 统一返回结构

后端统一使用：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

失败时 `code` 为非零错误码，`message` 为错误说明。

## 2. 认证接口

### `POST /api/auth/login`

请求示例：

```json
{
  "username": "student",
  "password": "123456"
}
```

### `POST /api/auth/register`

请求示例：

```json
{
  "username": "student02",
  "password": "123456",
  "displayName": "李同学",
  "email": "student02@example.com",
  "phone": "13812345678",
  "targetSchool": "清华大学",
  "targetMajor": "计算机科学与技术"
}
```

### `GET /api/auth/me`

- 说明：获取当前登录用户信息

## 3. 学生端接口

### 首页与公告

- `GET /api/student/home/summary`
- `GET /api/notices`

### 学习计划

- `GET /api/student/plans`
- `POST /api/student/plans`
- `GET /api/student/plans/{id}`
- `GET /api/student/plans/{id}/tasks`
- `POST /api/student/plans/{id}/tasks`
- `PATCH /api/student/tasks/{id}/status`
- `POST /api/student/tasks/{id}/progress`
- `GET /api/student/reminders`

### 资料共享

- `GET /api/materials`
- `GET /api/student/materials/{id}`
- `GET /api/student/materials/mine`
- `GET /api/student/materials/categories`
- `POST /api/student/materials`
- `GET /api/student/materials/{id}/download`

### 讨论区

- `GET /api/posts`
- `POST /api/posts`
- `GET /api/posts/{id}`
- `DELETE /api/posts/{id}`
- `GET /api/posts/{id}/comments`
- `POST /api/posts/{id}/comments`
- `DELETE /api/posts/comments/{id}`

### 模拟考试

- `GET /api/student/exams/papers`
- `GET /api/student/exams/papers/{id}`
- `GET /api/student/exams/papers/{id}/questions`
- `POST /api/student/exams/submit`
- `GET /api/student/exams/records`
- `GET /api/student/exams/records/{id}`
- `GET /api/student/exams/wrong-questions`

### 院校与资讯

- `GET /api/schools`
- `GET /api/schools/{id}`
- `GET /api/majors`
- `GET /api/admissions`
- `GET /api/admissions/{id}`
- `GET /api/ratios`
- `GET /api/policies`

## 4. 管理员端接口

### 统计分析

- `GET /api/admin/stats/overview`

### 用户与社区管理

- `GET /api/admin/users`
- `PATCH /api/admin/users/{id}/enabled`
- `GET /api/admin/posts`
- `GET /api/admin/posts/{id}/comments`
- `DELETE /api/admin/posts/{id}`
- `DELETE /api/admin/posts/comments/{id}`

### 资料审核

- `GET /api/admin/materials`
- `GET /api/admin/materials/reviews/pending`
- `PATCH /api/admin/materials/{id}/review`

### 招生信息维护

- `GET /api/admin/admissions`
- `POST /api/admin/admissions`
- `PATCH /api/admin/admissions/{id}`
- `DELETE /api/admin/admissions/{id}`
- `GET /api/admin/ratios`
- `POST /api/admin/ratios`
- `PATCH /api/admin/ratios/{id}`
- `DELETE /api/admin/ratios/{id}`
- `GET /api/admin/policies`
- `POST /api/admin/policies`
- `PATCH /api/admin/policies/{id}`
- `DELETE /api/admin/policies/{id}`

### 试卷管理

- `GET /api/admin/papers`
- `POST /api/admin/papers`
- `GET /api/admin/questions`
- `POST /api/admin/questions`
- `GET /api/admin/questions/{id}/options`
- `POST /api/admin/questions/{id}/options`

### 轻量系统管理

- `GET /api/notices`
- `POST /api/admin/notices`
- `PATCH /api/admin/notices/{id}`
- `GET /api/admin/configs`
- `PATCH /api/admin/configs/{key}`
- `GET /api/admin/logs`

## 5. 典型请求示例

### 公告发布

`POST /api/admin/notices`

```json
{
  "title": "答辩演示提醒",
  "content": "请使用默认账号进入学生端和管理员端进行演示。",
  "targetRole": "ALL"
}
```

### 系统配置修改

`PATCH /api/admin/configs/site_name`

```json
{
  "configValue": "考研助手系统答辩版",
  "configDescription": "系统名称"
}
```

### 资料审核

`PATCH /api/admin/materials/2/review`

```json
{
  "reviewStatus": "APPROVED",
  "reviewComment": "内容完整，审核通过"
}
```

## 6. JWT 使用说明

- 请求头格式：`Authorization: Bearer <token>`
- 登录成功后前端自动保存令牌
- 管理员接口需要 `ADMIN` 角色
- 学生接口一般允许 `STUDENT` 和 `ADMIN` 访问
