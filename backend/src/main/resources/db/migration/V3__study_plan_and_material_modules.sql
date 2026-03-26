CREATE TABLE material_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(64) NOT NULL,
    description VARCHAR(255),
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

ALTER TABLE material
    ADD COLUMN category_id BIGINT NULL AFTER user_id,
    ADD COLUMN download_count INT NOT NULL DEFAULT 0 AFTER file_size,
    ADD COLUMN review_comment VARCHAR(500) NULL AFTER review_status,
    ADD COLUMN reviewer_id BIGINT NULL AFTER review_comment,
    ADD COLUMN reviewed_at DATETIME(6) NULL AFTER reviewer_id;

CREATE TABLE study_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    plan_name VARCHAR(128) NOT NULL,
    plan_description TEXT,
    start_date DATE,
    end_date DATE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_study_plan_user_id FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE TABLE study_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    task_name VARCHAR(128) NOT NULL,
    task_description TEXT,
    due_date DATE,
    status VARCHAR(32) NOT NULL,
    completed_at DATETIME(6),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_study_task_plan_id FOREIGN KEY (plan_id) REFERENCES study_plan(id),
    CONSTRAINT fk_study_task_user_id FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE TABLE study_progress (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT NOT NULL,
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    progress_note TEXT,
    progress_percent INT NOT NULL,
    study_minutes INT NOT NULL,
    recorded_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_study_progress_plan_id FOREIGN KEY (plan_id) REFERENCES study_plan(id),
    CONSTRAINT fk_study_progress_task_id FOREIGN KEY (task_id) REFERENCES study_task(id),
    CONSTRAINT fk_study_progress_user_id FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE TABLE study_reminder (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT NULL,
    task_id BIGINT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(128) NOT NULL,
    content VARCHAR(500) NOT NULL,
    reminder_type VARCHAR(32) NOT NULL,
    is_read BIT NOT NULL DEFAULT b'0',
    remind_at DATETIME(6),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_study_reminder_plan_id FOREIGN KEY (plan_id) REFERENCES study_plan(id),
    CONSTRAINT fk_study_reminder_task_id FOREIGN KEY (task_id) REFERENCES study_task(id),
    CONSTRAINT fk_study_reminder_user_id FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

ALTER TABLE material
    ADD CONSTRAINT fk_material_category_id FOREIGN KEY (category_id) REFERENCES material_category(id),
    ADD CONSTRAINT fk_material_reviewer_id FOREIGN KEY (reviewer_id) REFERENCES sys_user(id);

CREATE INDEX idx_material_category_id ON material(category_id);
CREATE INDEX idx_material_user_id ON material(user_id);
CREATE INDEX idx_study_plan_user_id ON study_plan(user_id);
CREATE INDEX idx_study_task_plan_id ON study_task(plan_id);
CREATE INDEX idx_study_task_user_id ON study_task(user_id);
CREATE INDEX idx_study_progress_task_id ON study_progress(task_id);
CREATE INDEX idx_study_reminder_user_id ON study_reminder(user_id);

INSERT INTO material_category (id, category_name, description, sort_order) VALUES
    (1, 'Public Course', 'Politics, English, mathematics and other public-course materials.', 1),
    (2, 'Professional Course', '408 and other major-specific review materials.', 2),
    (3, 'Interview Guide', 'Re-exam and interview preparation materials.', 3);

UPDATE material
SET category_id = CASE id
    WHEN 1 THEN 2
    WHEN 2 THEN 1
    WHEN 3 THEN 1
    ELSE 1
END,
review_comment = CASE review_status
    WHEN 'APPROVED' THEN '审核通过'
    WHEN 'PENDING' THEN '待审核'
    ELSE review_comment
END,
reviewer_id = CASE review_status
    WHEN 'APPROVED' THEN 1
    ELSE NULL
END,
reviewed_at = CASE review_status
    WHEN 'APPROVED' THEN CURRENT_TIMESTAMP(6)
    ELSE NULL
END
WHERE category_id IS NULL;

ALTER TABLE material
    MODIFY COLUMN category_id BIGINT NOT NULL;

INSERT INTO study_plan (id, user_id, plan_name, plan_description, start_date, end_date) VALUES
    (1, 2, '408 强化阶段计划', '按章节完成强化复习，并在阶段末进行整套训练。', '2026-04-01', '2026-05-31'),
    (2, 2, '英语作文冲刺计划', '整理模板、限时练习大小作文并复盘。', '2026-04-05', '2026-04-30');

INSERT INTO study_task (id, plan_id, user_id, task_name, task_description, due_date, status, completed_at) VALUES
    (1, 1, 2, '完成数据结构强化题', '完成线性表、树、图三部分强化题并整理错题。', '2026-04-10', 'DONE', '2026-04-09 21:00:00'),
    (2, 1, 2, '操作系统真题整理', '按进程、内存、文件系统三类整理近五年真题。', '2026-04-18', 'IN_PROGRESS', NULL),
    (3, 1, 2, '408 阶段套卷一', '周末完成一套 408 模拟卷并分析错误来源。', '2026-04-25', 'TODO', NULL),
    (4, 2, 2, '整理英语大作文模板', '按教育、科技、社会三类整理模板并背诵。', '2026-04-12', 'DONE', '2026-04-11 20:00:00'),
    (5, 2, 2, '限时练习英语小作文', '每次 20 分钟完成并记录常见表达错误。', '2026-04-16', 'TODO', NULL);

INSERT INTO study_progress (id, plan_id, task_id, user_id, progress_note, progress_percent, study_minutes, recorded_at) VALUES
    (1, 1, 1, 2, '完成数据结构强化题第一轮，重点复盘图的最短路径。', 100, 180, '2026-04-09 21:00:00'),
    (2, 1, 2, 2, '完成进程与线程部分真题整理。', 45, 120, '2026-04-12 22:00:00'),
    (3, 2, 4, 2, '大作文模板完成初版并做了一次默写。', 100, 90, '2026-04-11 20:00:00');

INSERT INTO study_reminder (id, plan_id, task_id, user_id, title, content, reminder_type, is_read, remind_at) VALUES
    (1, 1, NULL, 2, '学习计划已创建', '计划《408 强化阶段计划》已创建，请按周推进。', 'SYSTEM', b'0', '2026-04-01 09:00:00'),
    (2, 1, 2, 2, '任务到期提醒', '任务《操作系统真题整理》将于 2026-04-18 到期。', 'DUE', b'0', '2026-04-17 09:00:00'),
    (3, 2, 5, 2, '任务到期提醒', '任务《限时练习英语小作文》将于 2026-04-16 到期。', 'DUE', b'0', '2026-04-15 09:00:00'),
    (4, 1, 2, 2, '学习进度已记录', '任务《操作系统真题整理》新增一条学习进度记录。', 'SYSTEM', b'0', '2026-04-12 22:00:00');
