CREATE TABLE system_notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    target_role VARCHAR(32) NOT NULL DEFAULT 'ALL',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE TABLE system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(128) NOT NULL,
    config_value TEXT NOT NULL,
    config_description VARCHAR(255),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT uk_system_config_config_key UNIQUE (config_key)
);

CREATE TABLE operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    username VARCHAR(64),
    user_role VARCHAR(32),
    operation_module VARCHAR(64) NOT NULL,
    operation_type VARCHAR(64) NOT NULL,
    operation_content VARCHAR(255) NOT NULL,
    request_path VARCHAR(255),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_operation_log_user_id FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE INDEX idx_system_notice_target_role ON system_notice(target_role);
CREATE INDEX idx_operation_log_created_at ON operation_log(created_at);
CREATE INDEX idx_operation_log_module_type ON operation_log(operation_module, operation_type);

INSERT INTO system_notice (id, title, content, target_role) VALUES
    (1, '考研冲刺阶段提醒', '请按周检查学习计划完成情况，及时补齐未完成任务。', 'STUDENT'),
    (2, '后台审核规范', '管理员审核资料时请填写审核意见，确保演示数据完整。', 'ADMIN'),
    (3, '系统演示说明', '当前系统已完成主要业务模块，可使用默认账号进行功能演示。', 'ALL');

INSERT INTO system_config (id, config_key, config_value, config_description) VALUES
    (1, 'site_name', '考研助手系统', '系统名称'),
    (2, 'upload_max_size_mb', '50', '资料上传大小上限，单位 MB'),
    (3, 'exam_default_duration_minutes', '180', '模拟考试默认时长，单位分钟');

INSERT INTO operation_log (id, user_id, username, user_role, operation_module, operation_type, operation_content, request_path) VALUES
    (1, 1, 'admin', 'ADMIN', 'AUTH', 'LOGIN', '管理员登录系统', '/api/auth/login'),
    (2, 1, 'admin', 'ADMIN', 'MATERIAL', 'REVIEW', '审核通过资料《408 Key Topic Notes》', '/api/admin/materials/1/review'),
    (3, 2, 'student', 'STUDENT', 'PLAN', 'CREATE', '创建学习计划《408 强化阶段计划》', '/api/student/plans'),
    (4, 2, 'student', 'STUDENT', 'POST', 'CREATE', '发布帖子《How should I schedule 408 practice during the reinforcement phase?》', '/api/posts');
