# 低碳生活系统

## 项目简介

低碳生活系统是一个基于Spring Boot后端和uni-app前端的应用，旨在帮助用户记录日常活动（如步数、运动）并计算相应的碳减排量，同时提供排名功能，鼓励用户参与低碳生活。

## 技术栈

### 后端

- Spring Boot 2.x
- MyBatis
- MySQL
- Java 8+

### 前端

- uni-app
- Vue 2.x
- WeChat Mini Program

## 环境要求

- JDK 8+
- MySQL 5.7+
- Node.js 14+
- HBuilderX（用于uni-app开发）
- WeChat Developer Tools（用于小程序预览）

## 数据库配置

1. 创建数据库：
   ```sql
   CREATE DATABASE campus_carbon DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. 执行数据库表结构：
   ```sql
   -- 用户表
   CREATE TABLE `user` (
     `id` INT NOT NULL AUTO_INCREMENT,
     'username' VARCHAR(50) NOT NULL,
     `name` VARCHAR(50) NOT NULL,
     `password` VARCHAR(100) NOT NULL,
     PRIMARY KEY (`id`),
     UNIQUE KEY `uk_stu_no` (`stu_no`)
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

   -- 步数记录表
   CREATE TABLE `step_count` (
     `id` BIGINT NOT NULL AUTO_INCREMENT,
     `student_id` VARCHAR(20) NOT NULL,
     `date` DATE NOT NULL,
     `steps` INT NOT NULL,
     `calories` DOUBLE DEFAULT NULL,
     `duration` INT DEFAULT NULL,
     PRIMARY KEY (`id`),
     KEY `idx_student_date` (`student_id`, `date`)
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

   -- 运动记录表
   CREATE TABLE `sport_record` (
     `id` BIGINT NOT NULL AUTO_INCREMENT,
     `student_id` VARCHAR(20) NOT NULL,
     `sport_type` VARCHAR(50) NOT NULL,
     `amount` DOUBLE NOT NULL,
     `start_time` DATETIME NOT NULL,
     `end_time` DATETIME NOT NULL,
     PRIMARY KEY (`id`),
     KEY `idx_student_start` (`student_id`, `start_time`)
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
   ```
3. 插入测试数据：
   ```sql
   -- 插入测试用户
   INSERT INTO `student` (`stu_no`, `name`, `password`) VALUES
   ('111', '李杰', '123456'),
   ('222', '张三', '123456'),
   ('333', '李四', '123456');

   -- 插入测试步数数据
   INSERT INTO `step_count` (`student_id`, `date`, `steps`, `calories`, `duration`) VALUES
   ('111', '2026-03-21', 10000, 500, 60),
   ('111', '2026-03-22', 8000, 400, 45),
   ('222', '2026-03-21', 7000, 350, 40),
   ('222', '2026-03-22', 9000, 450, 50),
   ('333', '2026-03-21', 6000, 300, 35),
   ('333', '2026-03-22', 8500, 425, 48);

   -- 插入测试运动数据
   INSERT INTO `sport_record` (`student_id`, `sport_type`, `amount`, `start_time`, `end_time`) VALUES
   ('111', '跑步', 5.5, '2026-03-21 08:00:00', '2026-03-21 09:00:00'),
   ('111', '骑行', 15.0, '2026-03-22 16:00:00', '2026-03-22 17:30:00'),
   ('222', '跑步', 3.0, '2026-03-21 07:30:00', '2026-03-21 08:15:00'),
   ('333', '骑行', 10.0, '2026-03-22 15:00:00', '2026-03-22 16:00:00');
   ```

## 后端配置和启动

1. 配置数据库连接：
   - 编辑 `src/main/resources/application.yml` 文件
   - 修改数据库连接信息（用户名、密码）
2. 启动后端服务：
   ```bash
   # 在项目根目录执行
   mvn spring-boot:run
   ```
   或使用IDE启动 `CarbonApplication` 类
3. 后端服务默认运行在 `http://localhost:8080`

## 前端配置和启动

1. 配置API地址：
   - 编辑 `uni-app项目/utils/request.js` 文件
   - 修改 `baseURL` 为后端服务地址
2. 启动前端开发服务器：
   - 使用HBuilderX打开 `uni-app项目` 目录
   - 点击工具栏的 "运行" -> "运行到小程序模拟器" -> "微信开发者工具"
3. 预览小程序：
   - 打开微信开发者工具
   - 导入 `uni-app项目` 生成的 `unpackage/dist/dev/mp-weixin` 目录
   - 点击 "编译" 按钮预览

## 主要功能

1. **用户登录**：使用学号和密码登录
2. **步数记录**：手动输入每日步数，计算减碳量和积分
3. **运动记录**：记录运动类型、距离和时长
4. **减碳排名**：基于减碳量的排名系统，支持今日、本周、本月时间范围
5. **个人中心**：查看个人减碳数据和积分

## 项目结构

### 后端

```
src/
├── main/
│   ├── java/com/campus/carbon/
│   │   ├── controller/     # 控制器
│   │   ├── mapper/         # MyBatis映射器
│   │   ├── model/          # 数据模型
│   │   ├── service/        # 业务逻辑
│   │   └── CarbonApplication.java  # 应用入口
│   └── resources/
│       └── application.yml  # 配置文件
└── test/                   # 测试代码
```

### 前端

```
uni-app项目/
├── pages/                 # 页面
│   ├── index/             # 首页
│   ├── login/             # 登录页
│   ├── stepCount/         # 步数记录页
│   ├── sportRecord/       # 运动记录页
│   └── rank/              # 排名页
├── utils/                 # 工具类
│   └── request.js         # API请求工具
├── pages.json             # 页面配置
└── manifest.json          # 应用配置
```

## 注意事项

1. **数据库时区**：确保数据库连接配置中的 `serverTimezone` 设置为 `Asia/Shanghai`，避免日期不一致问题
2. **端口占用**：如果8080端口被占用，可使用以下命令杀死占用进程：
   ```bash
   # Windows
   netstat -ano | findstr :8080
   taskkill /PID <进程ID> /F
   ```
3. **前端开发**：使用HBuilderX进行uni-app开发，确保已安装相关插件
4. **小程序预览**：需要在微信开发者工具中配置小程序AppID，或使用测试号
5. **数据同步**：前端页面每次显示时都会刷新日期和数据，确保数据与当前日期同步

## 减碳计算规则

- **步数减碳**：100步 = 0.005kg CO₂
- **运动减碳**：1公里 = 0.1kg CO₂
- **积分计算**：每0.1kg减碳得10分

## 联系方式

如有问题，请联系项目维护人员。
