# 数据库设计文档

## 1. 概念设计说明

系统围绕“学生考研备考服务”建立数据模型，核心实体分为六类：

- 用户与权限：用户、角色、用户角色
- 学习管理：学习计划、学习任务、学习进度、提醒
- 资料共享：资料分类、资料、审核信息
- 模拟考试：试卷、题目、选项、答卷记录、答题记录、错题
- 社区与信息：帖子、评论、院校、专业、招生简章、报录比、政策资讯
- 轻量系统管理：系统公告、系统配置、操作日志

实体关系概述：

- 一个用户可拥有多个角色，通过 `sys_user_role` 建立多对多关系
- 一个学习计划属于一个用户，一个学习计划下可包含多个学习任务
- 一条学习任务可关联多条学习进度和提醒
- 一个资料由一个用户上传，管理员审核后改变状态
- 一张试卷包含多道题目，每道题目可有多个选项
- 一次模拟考试提交生成一条考试记录和多条答题记录
- 一个帖子可包含多条评论
- 一个院校可关联多个专业、招生简章、政策资讯与报录比记录

## 2. 逻辑设计

### 2.1 用户与权限相关表

#### `sys_role`

- 用途：存储角色定义
- 关键字段：
  - `role_code`：角色编码，取值如 `ADMIN`、`STUDENT`
  - `role_name`：角色名称

#### `sys_user`

- 用途：存储系统用户信息
- 关键字段：
  - `username`：登录账号，唯一
  - `password`：加密密码
  - `display_name`：显示名称
  - `email`、`phone`：联系方式
  - `target_school`、`target_major`：学生备考目标
  - `enabled`：是否启用

#### `sys_user_role`

- 用途：维护用户与角色的多对多关系
- 关键字段：
  - `user_id`
  - `role_id`

### 2.2 学习管理相关表

#### `study_plan`

- 用途：存储用户学习计划
- 关键字段：
  - `user_id`
  - `plan_name`
  - `plan_description`
  - `start_date`
  - `end_date`

#### `study_task`

- 用途：存储学习任务
- 关键字段：
  - `plan_id`
  - `user_id`
  - `task_name`
  - `task_description`
  - `due_date`
  - `status`
  - `completed_at`

#### `study_progress`

- 用途：存储任务进度记录
- 关键字段：
  - `plan_id`
  - `task_id`
  - `user_id`
  - `progress_note`
  - `progress_percent`
  - `study_minutes`
  - `recorded_at`

#### `study_reminder`

- 用途：存储系统提醒和到期提醒
- 关键字段：
  - `plan_id`
  - `task_id`
  - `user_id`
  - `title`
  - `content`
  - `reminder_type`
  - `is_read`
  - `remind_at`

### 2.3 资料共享相关表

#### `material_category`

- 用途：资料分类
- 关键字段：
  - `category_name`
  - `description`
  - `sort_order`

#### `material`

- 用途：资料上传与审核主表
- 关键字段：
  - `category_id`
  - `user_id`
  - `title`
  - `description`
  - `file_name`
  - `file_path`
  - `file_size`
  - `download_count`
  - `review_status`
  - `review_comment`
  - `reviewer_id`
  - `reviewed_at`

### 2.4 模拟考试相关表

#### `exam_paper`

- 用途：试卷信息
- 关键字段：
  - `title`
  - `description`
  - `paper_year`
  - `question_count`
  - `total_score`

#### `exam_question`

- 用途：试题信息
- 关键字段：
  - `paper_id`
  - `question_type`
  - `question_stem`
  - `correct_answer`
  - `analysis_text`
  - `score`
  - `sort_order`

#### `exam_option`

- 用途：题目选项
- 关键字段：
  - `question_id`
  - `option_label`
  - `option_content`
  - `sort_order`

#### `exam_record`

- 用途：用户考试记录
- 关键字段：
  - `paper_id`
  - `user_id`
  - `score`
  - `total_score`
  - `correct_count`
  - `total_count`

#### `exam_answer`

- 用途：存储每道题的提交答案
- 关键字段：
  - `record_id`
  - `question_id`
  - `selected_answer`
  - `correct`
  - `awarded_score`

#### `wrong_question`

- 用途：错题记录
- 关键字段：
  - `user_id`
  - `paper_id`
  - `question_id`
  - `latest_record_id`
  - `latest_answer`

### 2.5 社区与院校信息相关表

#### `post`

- 用途：讨论区帖子
- 关键字段：
  - `user_id`
  - `title`
  - `content`
  - `view_count`

#### `post_comment`

- 用途：帖子评论
- 关键字段：
  - `post_id`
  - `user_id`
  - `content`

#### `school`

- 用途：院校信息
- 关键字段：
  - `school_name`
  - `province`
  - `city`
  - `school_type`
  - `school_level`
  - `description`

#### `major`

- 用途：专业信息
- 关键字段：
  - `school_id`
  - `major_name`
  - `major_code`
  - `degree_type`
  - `description`

#### `admission_guide`

- 用途：招生简章
- 关键字段：
  - `school_id`
  - `major_id`
  - `title`
  - `content`
  - `guide_year`

#### `application_ratio`

- 用途：报录比
- 关键字段：
  - `school_id`
  - `major_id`
  - `ratio_year`
  - `apply_count`
  - `admit_count`
  - `ratio_value`

#### `policy_news`

- 用途：政策资讯
- 关键字段：
  - `school_id`
  - `major_id`
  - `title`
  - `content`
  - `source_name`
  - `published_date`

### 2.6 轻量系统管理相关表

#### `system_notice`

- 用途：系统公告
- 关键字段：
  - `title`
  - `content`
  - `target_role`

#### `system_config`

- 用途：系统配置键值对
- 关键字段：
  - `config_key`
  - `config_value`
  - `config_description`

#### `operation_log`

- 用途：记录关键操作日志
- 关键字段：
  - `user_id`
  - `username`
  - `user_role`
  - `operation_module`
  - `operation_type`
  - `operation_content`
  - `request_path`

## 3. 物理设计

### 3.1 命名规范

- 表名：小写下划线
- 字段名：小写下划线
- 索引名：`idx_` 前缀
- 唯一约束：`uk_` 前缀
- 外键：`fk_` 前缀

### 3.2 主键设计

- 所有核心业务表均使用 `BIGINT AUTO_INCREMENT` 主键
- 所有实体统一继承 `id + created_at + updated_at` 基础字段

### 3.3 索引设计

已有及本轮新增的代表性索引如下：

- `uk_sys_user_username`：保证用户名唯一
- `uk_sys_role_role_code`：保证角色编码唯一
- `idx_material_review_status`：提升资料审核查询效率
- `idx_material_category_id`、`idx_material_user_id`：提升资料分类与个人资料查询效率
- `idx_study_plan_user_id`：提升学习计划按用户查询效率
- `idx_study_task_plan_id`、`idx_study_task_user_id`：提升任务按计划和用户查询效率
- `idx_post_created_at`：提升帖子时间排序效率
- `idx_post_comment_post_id`：提升评论按帖子查询效率
- `idx_application_ratio_school_major_year`：提升报录比按院校专业年份查询效率
- `idx_policy_news_published_date`：提升政策资讯按发布时间查询效率
- `idx_system_notice_target_role`：提升公告按角色过滤效率
- `idx_operation_log_created_at`：提升日志倒序查询效率
- `idx_operation_log_module_type`：提升日志按模块和操作类型筛选效率

### 3.4 迁移脚本说明

- `V1__init_schema.sql`：基础表结构
- `V2__init_demo_data.sql`：基础演示数据
- `V3__study_plan_and_material_modules.sql`：学习计划与资料模块
- `V4__exam_module_mvp.sql`：模拟考试模块
- `V5__forum_and_school_info_modules.sql`：讨论区与院校信息模块
- `V6__stats_and_system_management.sql`：统计分析依赖数据、系统公告、系统配置、操作日志

## 4. 设计说明总结

- 后端字段全部采用英文命名，便于代码与数据库保持一致
- 前端展示统一转为中文标签，满足项目展示需求
- 统计分析模块直接基于业务表聚合，不额外构建冗余统计表
- 系统管理模块保持轻量实现，避免引入复杂审计系统或动态配置中心
