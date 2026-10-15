-- ============================================================
-- 医院门诊挂号缴费系统 - 数据库脚本
-- 数据库: MySQL 8.0+
-- 创建日期: 2026-08-28
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS hospital_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hospital_db;

-- ============================================================
-- 1. 用户表
-- ============================================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(64) NOT NULL COMMENT '密码（MD5加盐）',
    salt VARCHAR(32) NOT NULL COMMENT '盐值',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    role VARCHAR(20) NOT NULL COMMENT '角色：patient/doctor/cashier/admin',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用，1正常',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_username (username),
    KEY idx_phone (phone),
    KEY idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 2. 患者表
-- ============================================================
DROP TABLE IF EXISTS patient;
CREATE TABLE patient (
    patient_id VARCHAR(32) NOT NULL COMMENT '患者ID',
    user_id VARCHAR(32) DEFAULT NULL COMMENT '关联用户ID',
    id_card VARCHAR(18) NOT NULL COMMENT '身份证号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender VARCHAR(4) DEFAULT NULL COMMENT '性别',
    age INT DEFAULT NULL COMMENT '年龄',
    birthday VARCHAR(10) DEFAULT NULL COMMENT '出生日期',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    address VARCHAR(200) DEFAULT NULL COMMENT '地址',
    medical_card VARCHAR(30) DEFAULT NULL COMMENT '医保卡号',
    insurance_type VARCHAR(20) DEFAULT NULL COMMENT '医保类型',
    emergency_contact VARCHAR(50) DEFAULT NULL COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) DEFAULT NULL COMMENT '紧急联系人电话',
    allergy_history VARCHAR(500) DEFAULT NULL COMMENT '过敏史',
    past_history VARCHAR(500) DEFAULT NULL COMMENT '既往病史',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (patient_id),
    UNIQUE KEY uk_id_card (id_card),
    KEY idx_user_id (user_id),
    KEY idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者表';

-- ============================================================
-- 3. 科室表
-- ============================================================
DROP TABLE IF EXISTS department;
CREATE TABLE department (
    dept_id VARCHAR(32) NOT NULL COMMENT '科室ID',
    dept_name VARCHAR(50) NOT NULL COMMENT '科室名称',
    dept_code VARCHAR(20) DEFAULT NULL COMMENT '科室编码',
    location VARCHAR(100) DEFAULT NULL COMMENT '科室位置',
    description VARCHAR(500) DEFAULT NULL COMMENT '科室描述',
    leader_id VARCHAR(32) DEFAULT NULL COMMENT '科室主任ID',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    dept_type VARCHAR(20) DEFAULT NULL COMMENT '科室类型',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    status TINYINT DEFAULT 1 COMMENT '状态：0停用，1正常',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (dept_id),
    UNIQUE KEY uk_dept_name (dept_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室表';

-- ============================================================
-- 4. 医生表
-- ============================================================
DROP TABLE IF EXISTS doctor;
CREATE TABLE doctor (
    doctor_id VARCHAR(32) NOT NULL COMMENT '医生ID',
    user_id VARCHAR(32) DEFAULT NULL COMMENT '关联用户ID',
    dept_id VARCHAR(32) NOT NULL COMMENT '所属科室ID',
    name VARCHAR(50) NOT NULL COMMENT '医生姓名',
    gender VARCHAR(4) DEFAULT NULL COMMENT '性别',
    title VARCHAR(20) NOT NULL COMMENT '职称',
    specialty VARCHAR(200) DEFAULT NULL COMMENT '专长',
    fee DECIMAL(10,2) NOT NULL COMMENT '挂号费',
    intro VARCHAR(500) DEFAULT NULL COMMENT '医生简介',
    experience_years INT DEFAULT NULL COMMENT '从业年限',
    education VARCHAR(20) DEFAULT NULL COMMENT '学历',
    school VARCHAR(50) DEFAULT NULL COMMENT '毕业院校',
    status TINYINT DEFAULT 1 COMMENT '状态：0停诊，1正常',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (doctor_id),
    KEY idx_dept_id (dept_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生表';

-- ============================================================
-- 5. 排班表
-- ============================================================
DROP TABLE IF EXISTS schedule;
CREATE TABLE schedule (
    schedule_id VARCHAR(32) NOT NULL COMMENT '排班ID',
    doctor_id VARCHAR(32) NOT NULL COMMENT '医生ID',
    dept_id VARCHAR(32) NOT NULL COMMENT '科室ID',
    work_date DATE NOT NULL COMMENT '出诊日期',
    time_slot VARCHAR(20) NOT NULL COMMENT '时段：上午/下午/晚间',
    start_time VARCHAR(10) DEFAULT NULL COMMENT '开始时间',
    end_time VARCHAR(10) DEFAULT NULL COMMENT '结束时间',
    max_count INT NOT NULL COMMENT '最大号源数',
    remain_count INT NOT NULL COMMENT '剩余号源数',
    booked_count INT DEFAULT 0 COMMENT '已挂号数',
    fee DECIMAL(10,2) NOT NULL COMMENT '挂号费',
    schedule_type VARCHAR(20) DEFAULT '普通门诊' COMMENT '排班类型',
    clinic_room VARCHAR(50) DEFAULT NULL COMMENT '诊室',
    status TINYINT DEFAULT 1 COMMENT '状态：0停诊，1正常',
    cancel_reason VARCHAR(200) DEFAULT NULL COMMENT '停诊原因',
    version INT DEFAULT 0 COMMENT '版本号（乐观锁）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (schedule_id),
    KEY idx_doctor_date (doctor_id, work_date),
    KEY idx_dept_date (dept_id, work_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排班表';

-- ============================================================
-- 6. 挂号记录表
-- ============================================================
DROP TABLE IF EXISTS appointment;
CREATE TABLE appointment (
    appt_id VARCHAR(32) NOT NULL COMMENT '挂号ID',
    patient_id VARCHAR(32) NOT NULL COMMENT '患者ID',
    doctor_id VARCHAR(32) NOT NULL COMMENT '医生ID',
    dept_id VARCHAR(32) NOT NULL COMMENT '科室ID',
    schedule_id VARCHAR(32) NOT NULL COMMENT '排班ID',
    appt_time DATETIME NOT NULL COMMENT '挂号时间',
    visit_date DATE NOT NULL COMMENT '就诊日期',
    time_slot VARCHAR(20) NOT NULL COMMENT '就诊时段',
    queue_no INT NOT NULL COMMENT '排队号',
    fee DECIMAL(10,2) NOT NULL COMMENT '挂号费',
    pay_status TINYINT DEFAULT 0 COMMENT '支付状态：0未支付，1已支付，2已退费',
    visit_status VARCHAR(20) DEFAULT '待就诊' COMMENT '就诊状态',
    source VARCHAR(20) DEFAULT '线上' COMMENT '挂号来源',
    pay_method VARCHAR(20) DEFAULT NULL COMMENT '支付方式',
    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
    trade_no VARCHAR(64) DEFAULT NULL COMMENT '支付交易号',
    call_time DATETIME DEFAULT NULL COMMENT '叫号时间',
    visit_start_time DATETIME DEFAULT NULL COMMENT '就诊开始时间',
    visit_end_time DATETIME DEFAULT NULL COMMENT '就诊结束时间',
    cancel_time DATETIME DEFAULT NULL COMMENT '退号时间',
    cancel_reason VARCHAR(200) DEFAULT NULL COMMENT '退号原因',
    operator_id VARCHAR(32) DEFAULT NULL COMMENT '操作人ID',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (appt_id),
    KEY idx_patient_id (patient_id),
    KEY idx_doctor_visit (doctor_id, visit_date),
    KEY idx_schedule_id (schedule_id),
    KEY idx_visit_status (visit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='挂号记录表';

-- ============================================================
-- 7. 处方表
-- ============================================================
DROP TABLE IF EXISTS prescription;
CREATE TABLE prescription (
    presc_id VARCHAR(32) NOT NULL COMMENT '处方ID',
    appt_id VARCHAR(32) DEFAULT NULL COMMENT '关联挂号ID',
    patient_id VARCHAR(32) NOT NULL COMMENT '患者ID',
    doctor_id VARCHAR(32) NOT NULL COMMENT '医生ID',
    dept_id VARCHAR(32) DEFAULT NULL COMMENT '科室ID',
    presc_no VARCHAR(32) NOT NULL COMMENT '处方编号',
    presc_type VARCHAR(20) DEFAULT NULL COMMENT '处方类型',
    diagnosis VARCHAR(500) DEFAULT NULL COMMENT '诊断结果',
    chief_complaint VARCHAR(500) DEFAULT NULL COMMENT '主诉',
    present_illness VARCHAR(1000) DEFAULT NULL COMMENT '现病史',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '药品总金额',
    status VARCHAR(20) DEFAULT '待缴费' COMMENT '状态',
    remark VARCHAR(500) DEFAULT NULL COMMENT '医嘱备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开具时间',
    pay_time DATETIME DEFAULT NULL COMMENT '缴费时间',
    dispense_time DATETIME DEFAULT NULL COMMENT '取药时间',
    cancel_time DATETIME DEFAULT NULL COMMENT '作废时间',
    cancel_reason VARCHAR(200) DEFAULT NULL COMMENT '作废原因',
    update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (presc_id),
    UNIQUE KEY uk_presc_no (presc_no),
    KEY idx_patient_id (patient_id),
    KEY idx_appt_id (appt_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方表';

-- ============================================================
-- 8. 处方明细表
-- ============================================================
DROP TABLE IF EXISTS prescription_item;
CREATE TABLE prescription_item (
    item_id VARCHAR(32) NOT NULL COMMENT '明细ID',
    presc_id VARCHAR(32) NOT NULL COMMENT '处方ID',
    medicine_id VARCHAR(32) NOT NULL COMMENT '药品ID',
    medicine_code VARCHAR(30) DEFAULT NULL COMMENT '药品编码',
    medicine_name VARCHAR(100) NOT NULL COMMENT '药品名称',
    specification VARCHAR(50) DEFAULT NULL COMMENT '药品规格',
    medicine_type VARCHAR(20) DEFAULT NULL COMMENT '药品类型',
    quantity INT NOT NULL COMMENT '数量',
    unit VARCHAR(10) NOT NULL COMMENT '单位',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    usage_method VARCHAR(50) DEFAULT NULL COMMENT '用法',
    dosage VARCHAR(50) DEFAULT NULL COMMENT '用量',
    frequency VARCHAR(30) DEFAULT NULL COMMENT '频次',
    days INT DEFAULT NULL COMMENT '天数',
    remark VARCHAR(200) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (item_id),
    KEY idx_presc_id (presc_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方明细表';

-- ============================================================
-- 9. 支付记录表
-- ============================================================
DROP TABLE IF EXISTS payment;
CREATE TABLE payment (
    pay_id VARCHAR(32) NOT NULL COMMENT '支付ID',
    pay_no VARCHAR(32) NOT NULL COMMENT '支付单号',
    appt_id VARCHAR(32) DEFAULT NULL COMMENT '关联挂号ID',
    presc_id VARCHAR(32) DEFAULT NULL COMMENT '关联处方ID',
    patient_id VARCHAR(32) NOT NULL COMMENT '患者ID',
    business_type VARCHAR(20) NOT NULL COMMENT '业务类型',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    pay_method VARCHAR(20) NOT NULL COMMENT '支付方式',
    status VARCHAR(20) DEFAULT '待支付' COMMENT '支付状态',
    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
    trade_no VARCHAR(64) DEFAULT NULL COMMENT '第三方交易号',
    trade_status VARCHAR(20) DEFAULT NULL COMMENT '第三方支付状态',
    refund_time DATETIME DEFAULT NULL COMMENT '退费时间',
    refund_amount DECIMAL(10,2) DEFAULT NULL COMMENT '退费金额',
    refund_reason VARCHAR(200) DEFAULT NULL COMMENT '退费原因',
    refund_trade_no VARCHAR(64) DEFAULT NULL COMMENT '退费交易号',
    operator_id VARCHAR(32) DEFAULT NULL COMMENT '操作人ID',
    remark VARCHAR(500) DEFAULT NULL COMMENT '支付备注',
    callback_data TEXT DEFAULT NULL COMMENT '回调信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (pay_id),
    UNIQUE KEY uk_pay_no (pay_no),
    KEY idx_patient_id (patient_id),
    KEY idx_appt_id (appt_id),
    KEY idx_presc_id (presc_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- ============================================================
-- 初始数据
-- ============================================================

-- 管理员用户（密码: admin123，盐: abc123）
INSERT INTO sys_user (user_id, username, password, salt, real_name, phone, role, status)
VALUES ('admin001', 'admin', 'e10adc3949ba59abbe56e057f20f883e', 'abc123', '系统管理员', '13800000000', 'admin', 1);

-- 科室数据
INSERT INTO department (dept_id, dept_name, dept_code, location, description, dept_type, sort_order, status) VALUES
('dept001', '内科', 'NK', '门诊楼2楼', '内科涵盖心血管、呼吸、消化、内分泌等专业', '临床科室', 1, 1),
('dept002', '外科', 'WK', '门诊楼3楼', '外科涵盖普外、骨科、神经外科等专业', '临床科室', 2, 1),
('dept003', '儿科', 'EK', '门诊楼2楼', '儿科专注于儿童疾病的诊断和治疗', '临床科室', 3, 1),
('dept004', '妇产科', 'FCK', '门诊楼4楼', '妇产科涵盖妇科和产科专业', '临床科室', 4, 1),
('dept005', '眼科', 'YK', '门诊楼3楼', '眼科专注于眼部疾病的诊断和治疗', '临床科室', 5, 1),
('dept006', '口腔科', 'KQK', '门诊楼2楼', '口腔科涵盖牙体牙髓、牙周、口腔外科等', '临床科室', 6, 1),
('dept007', '皮肤科', 'PFK', '门诊楼3楼', '皮肤科专注于皮肤疾病的诊断和治疗', '临床科室', 7, 1),
('dept008', '中医科', 'ZYK', '门诊楼4楼', '中医科运用中医理论进行疾病诊断和治疗', '临床科室', 8, 1);

-- 医生数据
INSERT INTO doctor (doctor_id, user_id, dept_id, name, gender, title, specialty, fee, intro, experience_years, education, status, sort_order) VALUES
('doc001', NULL, 'dept001', '张医生', '男', '主任医师', '心血管疾病、高血压', 30.00, '从事内科临床工作20年，擅长心血管疾病诊治', 20, '博士', 1, 1),
('doc002', NULL, 'dept001', '李医生', '女', '副主任医师', '呼吸系统疾病、哮喘', 20.00, '从事内科临床工作15年，擅长呼吸系统疾病', 15, '硕士', 1, 2),
('doc003', NULL, 'dept002', '王医生', '男', '主任医师', '普外科、胃肠疾病', 30.00, '从事外科临床工作25年，擅长普外科手术', 25, '博士', 1, 1),
('doc004', NULL, 'dept002', '赵医生', '男', '主治医师', '骨科、关节疾病', 10.00, '从事骨科临床工作10年，擅长关节疾病诊治', 10, '硕士', 1, 2),
('doc005', NULL, 'dept003', '陈医生', '女', '副主任医师', '小儿呼吸系统、新生儿疾病', 20.00, '从事儿科临床工作12年，擅长小儿呼吸系统疾病', 12, '硕士', 1, 1),
('doc006', NULL, 'dept004', '刘医生', '女', '主任医师', '妇科肿瘤、产科高危妊娠', 30.00, '从事妇产科临床工作22年，擅长妇科肿瘤诊治', 22, '博士', 1, 1),
('doc007', NULL, 'dept005', '孙医生', '男', '主治医师', '白内障、青光眼', 10.00, '从事眼科临床工作8年，擅长白内障手术', 8, '硕士', 1, 1),
('doc008', NULL, 'dept008', '周医生', '男', '主任医师', '中医内科、针灸', 30.00, '从事中医临床工作30年，擅长中医内科调理', 30, '博士', 1, 1);

-- 排班数据（未来7天）
INSERT INTO schedule (schedule_id, doctor_id, dept_id, work_date, time_slot, start_time, end_time, max_count, remain_count, booked_count, fee, schedule_type, clinic_room, status, version) VALUES
('sch001', 'doc001', 'dept001', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', '08:00', '12:00', 30, 30, 0, 30.00, '专家门诊', '内科诊室1', 1, 0),
('sch002', 'doc001', 'dept001', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '下午', '14:00', '17:30', 20, 20, 0, 30.00, '专家门诊', '内科诊室1', 1, 0),
('sch003', 'doc002', 'dept001', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', '08:00', '12:00', 40, 40, 0, 20.00, '普通门诊', '内科诊室2', 1, 0),
('sch004', 'doc003', 'dept002', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', '08:00', '12:00', 25, 25, 0, 30.00, '专家门诊', '外科诊室1', 1, 0),
('sch005', 'doc004', 'dept002', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', '08:00', '12:00', 35, 35, 0, 10.00, '普通门诊', '外科诊室2', 1, 0),
('sch006', 'doc005', 'dept003', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', '08:00', '12:00', 40, 40, 0, 20.00, '普通门诊', '儿科诊室1', 1, 0),
('sch007', 'doc006', 'dept004', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', '08:00', '12:00', 25, 25, 0, 30.00, '专家门诊', '妇产科诊室1', 1, 0),
('sch008', 'doc007', 'dept005', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', '08:00', '12:00', 30, 30, 0, 10.00, '普通门诊', '眼科诊室1', 1, 0),
('sch009', 'doc008', 'dept008', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', '08:00', '12:00', 20, 20, 0, 30.00, '专家门诊', '中医科诊室1', 1, 0),
('sch010', 'doc001', 'dept001', DATE_ADD(CURDATE(), INTERVAL 2 DAY), '上午', '08:00', '12:00', 30, 30, 0, 30.00, '专家门诊', '内科诊室1', 1, 0),
('sch011', 'doc002', 'dept001', DATE_ADD(CURDATE(), INTERVAL 2 DAY), '下午', '14:00', '17:30', 30, 30, 0, 20.00, '普通门诊', '内科诊室2', 1, 0),
('sch012', 'doc003', 'dept002', DATE_ADD(CURDATE(), INTERVAL 2 DAY), '上午', '08:00', '12:00', 25, 25, 0, 30.00, '专家门诊', '外科诊室1', 1, 0);

-- 收费员用户（密码: cashier123）
INSERT INTO sys_user (user_id, username, password, salt, real_name, phone, role, status)
VALUES ('cash001', 'cashier', 'e10adc3949ba59abbe56e057f20f883e', 'abc123', '收费员小王', '13900000001', 'cashier', 1);

-- 医生用户（密码: doctor123）
INSERT INTO sys_user (user_id, username, password, salt, real_name, phone, role, status) VALUES
('userdoc001', 'doctor1', 'e10adc3949ba59abbe56e057f20f883e', 'abc123', '张医生', '13900000002', 'doctor', 1),
('userdoc002', 'doctor2', 'e10adc3949ba59abbe56e057f20f883e', 'abc123', '李医生', '13900000003', 'doctor', 1);

-- 关联医生用户ID
UPDATE doctor SET user_id = 'userdoc001' WHERE doctor_id = 'doc001';
UPDATE doctor SET user_id = 'userdoc002' WHERE doctor_id = 'doc002';

-- 测试患者用户（密码: patient123）
INSERT INTO sys_user (user_id, username, password, salt, real_name, phone, role, status)
VALUES ('pat001', 'patient1', 'e10adc3949ba59abbe56e057f20f883e', 'abc123', '测试患者', '13700000001', 'patient', 1);

INSERT INTO patient (patient_id, user_id, id_card, name, gender, age, phone, medical_card, status)
VALUES ('patient001', 'pat001', '460100199001011234', '测试患者', '男', 34, '13700000001', 'MC20240001', 1);

SELECT '数据库初始化完成！' AS message;
