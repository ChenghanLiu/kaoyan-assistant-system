INSERT INTO sys_role (id, role_code, role_name) VALUES
    (1, 'ADMIN', 'Administrator'),
    (2, 'STUDENT', 'Student');

INSERT INTO sys_user (id, username, password, display_name, email, phone, target_school, target_major, enabled) VALUES
    (1, 'admin', '{noop}123456', 'System Admin', 'admin@kaoyan.local', '13800000000', NULL, NULL, b'1'),
    (2, 'student', '{noop}123456', 'Student Demo', 'student@kaoyan.local', '13900000000', 'Tsinghua University', 'Computer Science and Technology', b'1');

INSERT INTO sys_user_role (id, user_id, role_id) VALUES
    (1, 1, 1),
    (2, 2, 2);

INSERT INTO school (id, school_name, province, city, school_type, school_level, description) VALUES
    (1, 'Tsinghua University', 'Beijing', 'Beijing', 'Comprehensive', '985/211/Double First-Class', 'Strong computer science disciplines and highly competitive graduate admissions.'),
    (2, 'Beihang University', 'Beijing', 'Beijing', 'Engineering', '985/211/Double First-Class', 'Known for software engineering, computer science, and aerospace-related disciplines.'),
    (3, 'Huazhong University of Science and Technology', 'Hubei', 'Wuhan', 'Comprehensive', '985/211/Double First-Class', 'Popular graduate destination with solid engineering fundamentals.');

INSERT INTO major (id, school_id, major_name, major_code, degree_type, description) VALUES
    (1, 1, 'Computer Science and Technology', '081200', 'Academic', 'Focuses on data structures, operating systems, computer networks, and computer organization.'),
    (2, 2, 'Software Engineering', '083500', 'Professional', 'Emphasizes engineering practice, software architecture, and project delivery.'),
    (3, 3, 'Control Science and Engineering', '081100', 'Academic', 'Includes automation, pattern recognition, and systems engineering topics.'),
    (4, 3, 'Computer Science and Technology', '081200', 'Academic', 'A competitive program with stable enrollment scale and solid exam resources.');

INSERT INTO admission_guide (id, school_id, major_id, title, content, guide_year) VALUES
    (1, 1, 1, '2025 Graduate Admission Guide for Computer Science', 'Initial exam subjects include politics, English I, Mathematics I, and computer science fundamentals.', 2025),
    (2, 2, 2, '2025 Graduate Admission Guide for Software Engineering', 'The program highlights software systems design and engineering practice.', 2025),
    (3, 3, 4, '2025 Graduate Admission Guide for Computer Science', 'Interview rounds focus on coding ability, core theory, and project experience.', 2025);

INSERT INTO material (id, user_id, title, description, file_name, file_path, file_size, review_status) VALUES
    (1, 2, '408 Key Topic Notes', 'A concise summary for the reinforcement phase of the 408 exam.', 'demo-408-summary.pdf', 'demo-408-summary.pdf', 102400, 'APPROVED'),
    (2, 2, 'English Writing Templates', 'Templates for both long-form and short-form graduate English writing tasks.', 'demo-english-template.docx', 'demo-english-template.docx', 20480, 'PENDING'),
    (3, 1, 'Politics Sprint Checklist', 'An admin-prepared checklist for final-month politics review.', 'politics-checklist.pdf', 'politics-checklist.pdf', 51200, 'APPROVED');

INSERT INTO post (id, user_id, title, content, view_count) VALUES
    (1, 2, 'How should I schedule 408 practice during the reinforcement phase?', 'I finished the basics and now need a better rhythm for chapter drills and full mock sets.', 18),
    (2, 2, 'English reading review workflow', 'I classify mistakes by question type and track whether the error came from locating or elimination.', 26),
    (3, 1, 'Checklist for graduate re-exam preparation', 'Prepare self-introduction, project highlights, English speaking practice, and a clean resume summary.', 12);

INSERT INTO exam_paper (id, title, description, paper_year, question_count, total_score) VALUES
    (1, '408 Mock Paper I', 'A short mock paper for systems review and timed practice.', 2025, 20, 100);
