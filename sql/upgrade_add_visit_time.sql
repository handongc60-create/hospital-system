-- ============================================================
-- 升级脚本：为 appointment 表增加 visit_time 列（具体就诊时间点）
-- 执行前请先连接 hospital_db
-- ============================================================

USE hospital_db;

-- 增加具体就诊时间点列
ALTER TABLE appointment ADD COLUMN visit_time VARCHAR(10) DEFAULT NULL COMMENT '具体就诊时间点，如 08:30' AFTER time_slot;

-- 验证
SELECT '升级完成！appointment.visit_time 列已添加' AS message;
