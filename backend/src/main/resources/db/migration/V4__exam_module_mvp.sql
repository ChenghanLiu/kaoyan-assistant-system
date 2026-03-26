CREATE TABLE exam_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    paper_id BIGINT NOT NULL,
    question_type VARCHAR(32) NOT NULL,
    question_stem TEXT NOT NULL,
    correct_answer VARCHAR(255) NOT NULL,
    score INT NOT NULL,
    analysis_text TEXT,
    sort_order INT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_exam_question_paper_id FOREIGN KEY (paper_id) REFERENCES exam_paper(id)
);

CREATE TABLE exam_option (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_id BIGINT NOT NULL,
    option_label VARCHAR(16) NOT NULL,
    option_content TEXT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_exam_option_question_id FOREIGN KEY (question_id) REFERENCES exam_question(id),
    CONSTRAINT uk_exam_option_question_id_option_label UNIQUE (question_id, option_label)
);

CREATE TABLE exam_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    paper_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    score INT NOT NULL,
    total_score INT NOT NULL,
    correct_count INT NOT NULL,
    total_count INT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_exam_record_paper_id FOREIGN KEY (paper_id) REFERENCES exam_paper(id),
    CONSTRAINT fk_exam_record_user_id FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE TABLE exam_answer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    record_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    selected_answer VARCHAR(255),
    is_correct BIT NOT NULL DEFAULT b'0',
    awarded_score INT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_exam_answer_record_id FOREIGN KEY (record_id) REFERENCES exam_record(id),
    CONSTRAINT fk_exam_answer_question_id FOREIGN KEY (question_id) REFERENCES exam_question(id)
);

CREATE TABLE wrong_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    paper_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    latest_record_id BIGINT,
    latest_answer VARCHAR(255),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_wrong_question_user_id FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_wrong_question_paper_id FOREIGN KEY (paper_id) REFERENCES exam_paper(id),
    CONSTRAINT fk_wrong_question_question_id FOREIGN KEY (question_id) REFERENCES exam_question(id),
    CONSTRAINT fk_wrong_question_latest_record_id FOREIGN KEY (latest_record_id) REFERENCES exam_record(id),
    CONSTRAINT uk_wrong_question_user_id_question_id UNIQUE (user_id, question_id)
);

CREATE INDEX idx_exam_question_paper_id_sort_order ON exam_question(paper_id, sort_order, id);
CREATE INDEX idx_exam_option_question_id ON exam_option(question_id);
CREATE INDEX idx_exam_record_user_id_created_at ON exam_record(user_id, created_at);
CREATE INDEX idx_exam_answer_record_id ON exam_answer(record_id);
CREATE INDEX idx_wrong_question_user_id_updated_at ON wrong_question(user_id, updated_at);

UPDATE exam_paper
SET question_count = 5, total_score = 16
WHERE id = 1;

INSERT INTO exam_question (id, paper_id, question_type, question_stem, correct_answer, score, analysis_text, sort_order) VALUES
    (1, 1, 'SINGLE', '在操作系统中，进程从运行态因时间片用完而回到就绪态，这一转换通常由什么触发？', 'B', 3, '时间片轮转调度下，时钟中断到达后会触发调度，当前进程从运行态返回就绪队列。', 1),
    (2, 1, 'SINGLE', '在计算机网络中，负责端到端可靠传输并提供流量控制的协议是哪个？', 'C', 3, 'TCP 位于传输层，提供确认、重传、流量控制和拥塞控制等机制。', 2),
    (3, 1, 'MULTIPLE', '下面哪些属于数据库事务的 ACID 特性？', 'A,C,D', 4, 'ACID 分别是原子性、一致性、隔离性、持久性；高可用不属于 ACID。', 3),
    (4, 1, 'MULTIPLE', '下面哪些排序算法在平均情况下时间复杂度为 O(n log n)？', 'A,B,D', 4, '归并排序、堆排序、快速排序平均复杂度均为 O(n log n)，冒泡排序平均复杂度为 O(n^2)。', 4),
    (5, 1, 'JUDGE', '深度优先搜索实现拓扑排序时，若图中存在有向环，则无法得到合法拓扑序。', 'TRUE', 2, '拓扑排序只适用于有向无环图，存在有向环时不存在合法拓扑序。', 5);

INSERT INTO exam_option (id, question_id, option_label, option_content) VALUES
    (1, 1, 'A', '系统调用'),
    (2, 1, 'B', '时钟中断'),
    (3, 1, 'C', '磁盘中断'),
    (4, 1, 'D', '缺页异常'),
    (5, 2, 'A', 'IP'),
    (6, 2, 'B', 'UDP'),
    (7, 2, 'C', 'TCP'),
    (8, 2, 'D', 'ARP'),
    (9, 3, 'A', '原子性'),
    (10, 3, 'B', '高可用'),
    (11, 3, 'C', '隔离性'),
    (12, 3, 'D', '持久性'),
    (13, 4, 'A', '归并排序'),
    (14, 4, 'B', '堆排序'),
    (15, 4, 'C', '冒泡排序'),
    (16, 4, 'D', '快速排序'),
    (17, 5, 'TRUE', '正确'),
    (18, 5, 'FALSE', '错误');
