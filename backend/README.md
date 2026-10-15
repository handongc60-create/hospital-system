# 医院门诊挂号缴费系统

基于 Spring Boot + MyBatis-Plus + Vue 3 的医院门诊挂号缴费系统后端服务。

## 项目简介

本系统是一个面向医院门诊场景的挂号缴费一体化管理平台，支持患者线上预约挂号、现场挂号、多渠道缴费，医生叫号看诊、开具处方，收费员现场收费、退费，管理员科室管理、医生排班、数据统计等功能。

## 技术栈

### 后端
- **框架**: Spring Boot 2.7.18
- **ORM**: MyBatis-Plus 3.5.3.1
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **连接池**: Druid 1.2.16
- **认证**: JWT (jjwt 0.11.5)
- **工具类**: Hutool 5.8.20
- **接口文档**: Knife4j 4.1.0
- **构建工具**: Maven

### 前端（建议）
- Vue 3 + Element Plus
- Vite
- Axios

## 项目结构

```
hospital-registration-system/
├── pom.xml                                    # Maven配置
├── sql/
│   └── schema.sql                             # 数据库脚本（建表+初始数据）
└── src/main/
    ├── java/com/hospital/
    │   ├── HospitalApplication.java           # 启动类
    │   ├── common/                            # 公共类
    │   │   ├── Result.java                    # 统一响应结果
    │   │   └── ResultCode.java                # 响应状态码枚举
    │   ├── config/                            # 配置类
    │   │   ├── MybatisPlusConfig.java         # MyBatis-Plus配置（分页+乐观锁）
    │   │   ├── CorsConfig.java                # 跨域配置
    │   │   ├── RedisConfig.java               # Redis配置
    │   │   └── MyMetaObjectHandler.java       # 自动填充处理器
    │   ├── controller/                        # 控制层（RESTful API）
    │   │   ├── UserController.java            # 用户管理
    │   │   ├── AppointmentController.java     # 挂号管理
    │   │   ├── PaymentController.java         # 支付管理
    │   │   ├── PrescriptionController.java    # 处方管理
    │   │   └── DepartmentController.java      # 科室管理
    │   ├── dto/                               # 数据传输对象
    │   │   ├── LoginDTO.java
    │   │   ├── RegisterDTO.java
    │   │   ├── AppointmentDTO.java
    │   │   ├── PaymentDTO.java
    │   │   └── PrescriptionDTO.java
    │   ├── entity/                            # 实体类
    │   │   ├── User.java
    │   │   ├── Patient.java
    │   │   ├── Doctor.java
    │   │   ├── Department.java
    │   │   ├── Schedule.java
    │   │   ├── Appointment.java
    │   │   ├── Prescription.java
    │   │   ├── PrescriptionItem.java
    │   │   └── Payment.java
    │   ├── exception/                         # 异常处理
    │   │   ├── BusinessException.java         # 业务异常
    │   │   └── GlobalExceptionHandler.java    # 全局异常处理器
    │   ├── mapper/                            # 数据访问层
    │   │   ├── UserMapper.java
    │   │   ├── PatientMapper.java
    │   │   ├── DoctorMapper.java
    │   │   ├── DepartmentMapper.java
    │   │   ├── ScheduleMapper.java
    │   │   ├── AppointmentMapper.java
    │   │   ├── PrescriptionMapper.java
    │   │   ├── PrescriptionItemMapper.java
    │   │   └── PaymentMapper.java
    │   ├── service/                           # 业务逻辑层
    │   │   ├── UserService.java
    │   │   ├── AppointmentService.java
    │   │   ├── PaymentService.java
    │   │   ├── PrescriptionService.java
    │   │   └── impl/                          # 业务实现
    │   │       ├── UserServiceImpl.java
    │   │       ├── AppointmentServiceImpl.java
    │   │       ├── PaymentServiceImpl.java
    │   │       └── PrescriptionServiceImpl.java
    │   ├── util/                              # 工具类
    │   │   ├── JwtUtil.java                   # JWT工具
    │   │   └── MD5Util.java                   # MD5加密工具
    │   └── vo/                                # 视图对象
    │       ├── LoginVO.java
    │       ├── AppointmentVO.java
    │       ├── ScheduleVO.java
    │       └── PaymentVO.java
    └── resources/
        ├── application.yml                     # 应用配置
        └── mapper/                             # MyBatis XML映射
            ├── ScheduleMapper.xml
            └── AppointmentMapper.xml
```

## 快速开始

### 1. 环境要求
- JDK 11+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 2. 数据库初始化
```bash
# 登录MySQL
mysql -u root -p

# 执行数据库脚本
source /path/to/hospital-registration-system/sql/schema.sql
```

### 3. 修改配置
编辑 `src/main/resources/application.yml`，修改数据库和Redis连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hospital_db?...
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
```

### 4. 编译运行
```bash
# 编译
mvn clean package -DskipTests

# 运行
java -jar target/hospital-registration-system-1.0.0.jar

# 或使用Maven运行
mvn spring-boot:run
```

### 5. 访问接口文档
启动成功后访问：http://localhost:8080/api/doc.html

## 核心功能

### 1. 用户管理
- 用户登录（JWT认证）
- 患者注册
- 修改密码
- 获取当前用户信息

### 2. 挂号管理
- 预约挂号（乐观锁保证号源不超卖）
- 取消预约（退号，含时限校验）
- 查询可预约排班
- 查询挂号记录
- 医生叫号
- 完成看诊
- 查询医生当日挂号队列

### 3. 支付管理
- 创建支付订单（支持微信/支付宝/医保卡/现金）
- 支付回调处理（幂等性保证）
- 退费处理
- 查询支付记录
- 现场收费

### 4. 处方管理
- 开具处方（含处方明细）
- 查询处方详情
- 查询处方明细
- 作废处方
- 查询患者处方列表

### 5. 科室管理
- 查询所有科室
- 查询科室详情
- 查询科室下医生列表

## 核心设计亮点

### 1. 号源并发安全
采用数据库**乐观锁**（version字段）保证高并发场景下号源不超卖：
```java
@Update("UPDATE schedule SET remain_count = remain_count - 1, version = version + 1 " +
        "WHERE schedule_id = #{scheduleId} AND version = #{version} AND remain_count > 0")
int decreaseRemainCount(@Param("scheduleId") String scheduleId,
                         @Param("version") Integer version);
```

### 2. 支付幂等性
支付回调处理时先查询支付状态，已支付的订单直接返回成功，避免重复处理：
```java
if ("已支付".equals(payment.getStatus())) {
    return Result.success("重复回调，已处理");
}
```

### 3. 统一异常处理
通过 `@RestControllerAdvice` 全局捕获异常，统一返回格式：
- 业务异常（BusinessException）
- 参数校验异常
- 系统异常

### 4. 自动填充
通过 MyBatis-Plus 的 `MetaObjectHandler` 自动填充创建时间和更新时间。

### 5. 逻辑删除
所有表均支持逻辑删除（deleted字段），数据不物理删除，便于数据追溯。

## API接口列表

### 用户模块
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /user/login | 用户登录 |
| POST | /user/register | 患者注册 |
| POST | /user/logout | 用户登出 |
| POST | /user/change-password | 修改密码 |
| GET | /user/info | 获取当前用户信息 |

### 挂号模块
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /appointment/make | 预约挂号 |
| POST | /appointment/cancel/{apptId} | 取消预约 |
| GET | /appointment/schedules | 查询可预约排班 |
| GET | /appointment/my | 查询我的挂号记录 |
| GET | /appointment/{apptId} | 查询挂号详情 |
| POST | /appointment/call | 医生叫号 |
| POST | /appointment/finish/{apptId} | 完成看诊 |
| GET | /appointment/doctor/queue | 查询医生挂号队列 |

### 支付模块
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /payment/create | 创建支付订单 |
| POST | /payment/callback | 支付回调 |
| POST | /payment/refund/{payId} | 退费处理 |
| GET | /payment/{payId} | 查询支付详情 |
| GET | /payment/my | 查询我的支付记录 |
| POST | /payment/onsite | 现场收费 |

### 处方模块
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /prescription/create | 开具处方 |
| GET | /prescription/{prescId} | 查询处方详情 |
| GET | /prescription/{prescId}/items | 查询处方明细 |
| GET | /prescription/my | 查询我的处方 |
| POST | /prescription/cancel/{prescId} | 作废处方 |

### 科室模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /department/list | 查询所有科室 |
| GET | /department/{deptId} | 查询科室详情 |
| GET | /department/{deptId}/doctors | 查询科室医生 |

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 医生 | doctor1 | doctor123 |
| 医生 | doctor2 | doctor123 |
| 收费员 | cashier | cashier123 |
| 患者 | patient1 | patient123 |

## 开发说明

1. 本项目为后端服务，前端可基于 Vue 3 + Element Plus 自行开发
2. 第三方支付（微信/支付宝）为模拟实现，实际使用需接入对应SDK
3. 患者ID与用户ID的关联查询需在实际项目中完善（当前简化处理）
4. 建议在生产环境中使用 HTTPS、加强密码加密策略、增加接口限流

## 许可证

MIT License
