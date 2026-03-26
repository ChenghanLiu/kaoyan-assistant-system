CREATE TABLE post_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_post_comment_post_id FOREIGN KEY (post_id) REFERENCES post(id),
    CONSTRAINT fk_post_comment_user_id FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE TABLE application_ratio (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    school_id BIGINT NOT NULL,
    major_id BIGINT NOT NULL,
    ratio_year INT NOT NULL,
    apply_count INT NOT NULL,
    admit_count INT NOT NULL,
    ratio_value VARCHAR(32) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_application_ratio_school_id FOREIGN KEY (school_id) REFERENCES school(id),
    CONSTRAINT fk_application_ratio_major_id FOREIGN KEY (major_id) REFERENCES major(id)
);

CREATE TABLE policy_news (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    school_id BIGINT,
    major_id BIGINT,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    source_name VARCHAR(128),
    published_date DATE NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_policy_news_school_id FOREIGN KEY (school_id) REFERENCES school(id),
    CONSTRAINT fk_policy_news_major_id FOREIGN KEY (major_id) REFERENCES major(id)
);

CREATE INDEX idx_post_comment_post_id ON post_comment(post_id);
CREATE INDEX idx_application_ratio_school_major_year ON application_ratio(school_id, major_id, ratio_year);
CREATE INDEX idx_policy_news_published_date ON policy_news(published_date);

INSERT INTO post_comment (id, post_id, user_id, content) VALUES
    (1, 1, 1, '建议把章节题和整卷模拟拆成 4:1 的节奏，周末做一次完整复盘。'),
    (2, 1, 2, '我会把数据结构和计组交替刷，避免同一科目疲劳。'),
    (3, 2, 1, '阅读错因标签法很实用，最好再加一个生词回看清单。');

INSERT INTO application_ratio (id, school_id, major_id, ratio_year, apply_count, admit_count, ratio_value) VALUES
    (1, 1, 1, 2025, 420, 42, '10:1'),
    (2, 2, 2, 2025, 315, 63, '5:1'),
    (3, 3, 4, 2025, 286, 52, '5.5:1');

INSERT INTO policy_news (id, school_id, major_id, title, content, source_name, published_date) VALUES
    (1, NULL, NULL, '2026 考研报名时间提醒', '请提前准备学历认证、照片和目标专业信息，避开集中报名高峰。', '研招网', '2026-03-20'),
    (2, 1, 1, '清华大学计算机专业复试说明', '关注复试材料提交节点、英语问答和项目经历说明。', '清华大学研招办', '2026-03-18'),
    (3, 2, 2, '北航软件工程调剂政策解读', '建议结合方向需求和调剂系统开放时间准备备选方案。', '北航研究生院', '2026-03-15');
