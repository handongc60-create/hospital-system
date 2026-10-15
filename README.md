# 医院门诊挂号缴费系统

> 软件工程专业课程 / 开源实践项目 · 面向「门诊挂号 → 缴费 → 看诊 → 开方」全流程的轻量级医院门诊管理系统。
> 技术栈：Spring Boot 2.7 + MyBatis-Plus + MySQL 8.0 + Redis（可选） + Vue 2 + Element UI

![后端](https://img.shields.io/badge/后端-Spring%20Boot%202.7-6DB33F) ![ORM](https://img.shields.io/badge/ORM-MyBatis--Plus%203.5-blue) ![数据库](https://img.shields.io/badge/数据库-MySQL%208.0-4479A1) ![前端](https://img.shields.io/badge/前端-Vue2%20%2B%20Element%20UI-4FC08D) ![认证](https://img.shields.io/badge/认证-JWT%20Token-orange)

---

## 📌 项目简介

本项目实现了一个**医院门诊挂号缴费系统**，覆盖患者在线预约挂号、医生叫号看诊、开方缴费、收费员收费与退款、管理员基础数据维护等核心业务，采用前后端分离架构，包含**患者端、医生端、收费员端、管理员端**四类角色。

- **前端**：Vue 2 + Element UI 通过 **CDN 引入，免构建、免 Node 环境**，浏览器直接打开即可运行，便于演示与部署。
- **后端**：Spring Boot + MyBatis-Plus + MySQL，RESTful 接口 + JWT 无状态鉴权。
- **并发与安全**：号源扣减采用乐观锁，支付单唯一索引保证幂等；密码 MD5 加盐存储；逻辑删除统一约定。

---

## ✨ 功能特性

### 患者端
- ✅ 在线预约挂号（四步向导：选科室 → 选医生 → 选时间 → 确认）
- ✅ 号源实时查询、剩余号源显示
- ✅ 我的挂号记录查询、取消退号（限时 2 小时）
- ✅ 同一时段重复挂号拦截
- ✅ 处方与缴费记录查询

### 医生端
- ✅ 今日挂号队列查看
- ✅ 叫号 / 完成看诊
- ✅ 开具处方与处方明细（诊断、主诉、药品、用法用量）
- ✅ 处方作废
- ✅ 叫号大屏展示

### 收费员端
- ✅ 现场缴费（现金）
- ✅ 缴费记录分页查询
- ✅ 退费处理、支付单状态流转

### 管理员端
- ✅ 用户管理（增删改查、角色控制）
- ✅ 科室管理
- ✅ 数据统计概览

---

## 🛠 技术架构

### 后端技术栈
| 技术 | 说明 |
|------|------|
| Spring Boot 2.7.18 | 主框架 |
| MyBatis-Plus 3.5.3 | ORM，含乐观锁、逻辑删除、分页 |
| MySQL 8.0 + Druid | 数据库与连接池 |
| JWT | 无状态登录鉴权 |
| Redis | 号源缓存（可选，非必需） |
| Knife4j | 在线接口文档（Swagger 增强） |

### 前端技术栈
| 技术 | 说明 |
|------|------|
| Vue 2.6.14 | 前端框架（CDN 引入） |
| Element UI | UI 组件库（CDN 引入） |
| Axios | HTTP 请求封装（CDN 引入） |
| 原生 CSS3 | 响应式布局 |

> 前端通过 `<script>` 标签 CDN 引入，**无需 Node.js / npm / 构建工具**，双击 HTML 即可运行。

### 三层架构
```
Controller（控制层）→ Service（业务层）→ Mapper（数据层）→ MySQL
```

### 核心设计亮点
- **号源并发安全**：`schedule` 表使用 `@Version` 乐观锁，抢号/并发时保证号源不超卖
- **支付幂等**：`payment` 表支付单号唯一索引，防止重复支付
- **统一响应**：`Result<T>` 统一返回格式 + 全局异常处理
- **安全**：密码 MD5 加盐、JWT Token 鉴权、逻辑删除

---

## 🚀 快速开始

### 环境要求
| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 11+ | 运行后端 |
| Maven | 3.6+ | 构建后端（IDEA 自带） |
| MySQL | 8.0+ | 数据库 |
| 浏览器 | Chrome / Edge | 运行前端页面 |

### 第 1 步：导入数据库
1. 使用 Navicat / MySQL Workbench / 命令行连接本地 MySQL
2. 执行项目根目录 `sql/schema.sql` 脚本，自动创建 `hospital_db` 库、9 张表和测试数据
```bash
mysql -u root -p < sql/schema.sql
```

### 第 2 步：启动后端
1. 用 IDEA 打开 `backend` 目录，等待 Maven 下载依赖
2. 打开 `backend/src/main/resources/application.yml`
3. 数据库密码默认读取环境变量 `MYSQL_PASSWORD`，未设置时回退到示例密码 `123456`。如本机密码不同，请修改该行或设置环境变量：
```yaml
# application.yml
spring:
  datasource:
    password: ${MYSQL_PASSWORD:123456}   # 改成你本机 MySQL 密码
```
> Windows 设置环境变量：`set MYSQL_PASSWORD=你的密码`（PowerShell: `$env:MYSQL_PASSWORD="你的密码"`）
4. 运行启动类 `com.hospital.HospitalApplication`

### 第 3 步：打开前端
1. 进入 `frontend` 目录
2. 双击 `login.html` 用浏览器打开（前端依赖 CDN，需联网）
3. 后端已配置 CORS，前端可直接调用后端 API

### 验证是否成功
- 后端接口文档：`http://localhost:8080/api/doc.html`
- 健康检查：`http://localhost:8080/api/department/list`（应返回科室列表 JSON）
- 前端登录页：双击 `frontend/login.html`

---

## 👤 测试账号

所有账号密码统一为：**123456**（或你在 application.yml 中配置的 MYSQL_PASSWORD）

| 用户名 | 角色 | 说明 |
|--------|------|------|
| `admin` | 管理员 | 用户管理、科室管理 |
| `doctor1` | 医生 | 张医生（内科主任医师） |
| `doctor2` | 医生 | 李医生（内科副主任医师） |
| `cashier` | 收费员 | 收费、退款 |
| `patient1` | 患者 | 在线预约挂号 |

---

## 📊 数据库设计（9 张表）

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| sys_user | 用户表 | user_id, username, password(salt+MD5), role |
| patient | 患者表 | patient_id, user_id, id_card, name, phone, insurance |
| doctor | 医生表 | doctor_id, user_id, dept_id, name, title, fee |
| department | 科室表 | dept_id, dept_name, location, description |
| schedule | 排班表 | schedule_id, doctor_id, work_date, time_slot, remain_count, version |
| appointment | 挂号记录表 | appt_id, patient_id, doctor_id, visit_date, queue_no, visit_status |
| prescription | 处方表 | presc_id, appt_id, diagnosis, total_amount, status |
| prescription_item | 处方明细表 | item_id, presc_id, medicine_name, quantity, unit_price |
| payment | 支付记录表 | pay_id, pay_no(唯一), appt_id, amount, pay_method, status |

---

## 📁 项目结构

```
hospital-system/
├── backend/                  # 后端（Spring Boot + Java）
│   ├── src/main/java/com/hospital/
│   │   ├── controller/       # 控制器层（6个，30+接口）
│   │   ├── service/          # 业务层（接口+实现）
│   │   ├── mapper/           # 数据访问层
│   │   ├── entity/           # 实体类
│   │   ├── dto/ vo/          # 传输/视图对象
│   │   ├── config/           # 配置（MyBatis-Plus、跨域、Redis）
│   │   └── util/             # 工具（JWT、MD5）
│   ├── src/main/resources/
│   │   ├── mapper/           # MyBatis XML 映射
│   │   └── application.yml   # 应用配置
│   └── pom.xml               # Maven 依赖
├── frontend/                 # 前端（Vue2 + Element UI，CDN 免构建）
│   ├── login.html
│   ├── index.html
│   ├── css/style.css
│   └── js/api.js
├── sql/schema.sql            # 数据库建表脚本 + 初始数据
├── docs/                     # 项目设计文档
└── README.md
```

---

## 🔧 开发规范

- **分层**：Controller → Service → Mapper 三层架构
- **统一响应**：`Result<T>`，包含 code / message / data
- **异常处理**：全局 `GlobalExceptionHandler`
- **号源并发**：乐观锁 `@Version`
- **支付幂等**：支付单号唯一索引
- **认证**：JWT Token 无状态鉴权

---

## 🎯 项目难点与个人思考

1. **并发抢号不超卖**：使用乐观锁（`@Version`）+ 唯一索引，保证高并发下号源扣减正确。
2. **支付幂等**：支付单号唯一索引，重复支付/回调不会产生重复订单。
3. **密码安全**：MD5 加盐存储 + JWT 无状态鉴权，避免明文密码。
4. **前端免构建**：通过 CDN 引入 Vue/Element UI，降低部署门槛，双击即可演示。

---

## 🧭 后续规划（v2.0）

- [ ] 排班管理、医生管理、药品目录后台化（不再依赖手写 SQL）
- [ ] 扫码支付（微信 / 支付宝）模拟收银台
- [ ] 检查检验申请单与报告查询
- [ ] 药房发药 + 药师审方
- [ ] 电子发票、消息通知、就诊评价
- [ ] 运营统计大屏
`

---

## 📝 说明

- 本项目为**开源学习/课程设计项目**，数据库配置为本地示例，仅供学习演示
- 数据库密码通过环境变量注入，仓库中不提交明文敏感配置
- 部署文档见 README 各节，设计文档见 `docs/`

## 👨‍💻 作者

- 专业：软件工程 · 计算机科学与技术学院
- 开发时间：2026 年
- 项目定位：课程设计 / 开源实践项目
