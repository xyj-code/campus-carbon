校园低碳生活管理系统启动说明
================================

一、项目简介
--------------------------------
本项目是一个面向高校学生的低碳生活管理平台，主要用于记录步数、运动、健康数据和低碳任务，并将用户行为换算为减碳量和积分。

项目包含后端和前端两部分：
1. 后端：Spring Boot + MyBatis + MySQL
2. 前端：uni-app + Vue2，小程序端页面

当前项目还加入了 Agent Planner 功能，可以根据用户步数、健康档案、任务进度、用户备注和当前位置，生成当天低碳行动计划，并支持开始、完成、跳过行动。


二、运行环境
--------------------------------
1. JDK 8 或以上
2. Maven 3.x
3. MySQL 5.7 或 MySQL 8.x
4. HBuilderX
5. 微信开发者工具


三、数据库启动准备
--------------------------------
1. 打开 MySQL，新建数据库：

CREATE DATABASE campus_carbon DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

2. 导入项目 sql 目录下的数据库脚本。

建议使用 Navicat、DataGrip 或 MySQL Workbench 导入，也可以使用命令行导入。

推荐导入顺序：

1. user.sql
2. step_count.sql
3. sport_record.sql
4. points_record.sql
5. product.sql
6. exchange_record.sql
7. task_template.sql
8. user_task.sql
9. activity_topic.sql
10. activity_topic_task_codes.sql
11. activity_topic_mainline_refresh.sql
12. project.sql
13. user_project.sql
14. user_tree.sql
15. ai_suggest.sql
16. agent_session.sql
17. agent_action_log.sql
18. agent_memory.sql

如果使用命令行，可以在项目根目录执行类似命令：

mysql -u root -p campus_carbon < sql/user.sql

其他 sql 文件按上面的顺序继续导入即可。


四、后端启动方式
--------------------------------
1. 打开后端配置文件：

src/main/resources/application.yml

2. 检查数据库连接配置：

spring.datasource.url=jdbc:mysql://localhost:3306/campus_carbon
spring.datasource.username=root
spring.datasource.password=123456

如果你本机 MySQL 用户名或密码不同，需要改成自己的配置。

3. 检查 AI 和地图配置：

carbon.ai.api-key
carbon.ai.endpoint
carbon.amap.web-key
carbon.amap.js-key
carbon.amap.js-security-code

如果只是本地演示普通功能，可以先不重点关注；如果要演示 AI 建议、Agent Planner 或附近运动场地推荐，需要确保这些 key 可用。

4. 启动后端。

方式一：使用命令行，在项目根目录执行：

mvn spring-boot:run

方式二：使用 IDEA 打开项目，运行：

src/main/java/com/campus/carbon/CarbonApplication.java

5. 后端启动成功后，默认访问地址为：

http://localhost:8080

前端请求接口的基础地址为：

http://localhost:8080/api


五、前端启动方式
--------------------------------
1. 使用 HBuilderX 打开前端目录：

uni-app项目

2. 检查前端接口地址：

uni-app项目/utils/request.js

当前配置为：

const baseUrl = 'http://localhost:8080/api';

如果后端不是运行在 8080 端口，需要同步修改这里。

3. 在 HBuilderX 中运行：

运行 -> 运行到小程序模拟器 -> 微信开发者工具

4. 如果微信开发者工具需要导入目录，一般导入：

uni-app项目/unpackage/dist/dev/mp-weixin

5. 后端和数据库都启动后，小程序端即可正常登录、注册和访问功能页面。


六、主要功能说明
--------------------------------
1. 登录与注册
用户可以通过账号密码登录，也可以在登录页进入注册流程。

2. 首页数据汇总
首页聚合展示用户积分、步数、排行榜、任务、活动和 AI 相关入口。

3. 步数统计
用户可以录入或查看每日步数，系统将步数写入 step_count 表，并按日期维护记录。

4. 运动记录
用户可以提交运动类型、距离和时间等信息，系统写入 sport_record 表。

5. 减碳排行
后端汇总步数和运动数据，统一换算为减碳量后生成用户排行榜。

6. 健康数据
支持录入身高、体重等身体指标，后端自动计算 BMI，并按日期维护健康档案。

7. AI 建议
系统可调用大模型接口，生成低碳生活建议和个性化健康建议。

8. Agent Planner
系统结合任务进度、健康数据、用户备注和当前位置，自动生成当天行动计划。
行动支持开始、完成、跳过，并会记录到 agent_session、agent_action_log 和 agent_memory 表。

9. 任务与活动中心
任务系统以 task_template 和 user_task 为核心，用户完成行为后自动同步任务进度并发放积分。
活动中心通过活动配置绑定任务主线，支持专题低碳活动展示。

10. 积分商城
用户可以使用积分兑换环保商品，系统会校验积分、扣减库存，并写入兑换记录。

11. 个人中心与碳账本
个人中心展示用户积分、成长值、减碳数据和个人历史记录。

12. 低碳保护区与证书
系统根据用户累计减碳量判断是否达到公益项目解锁门槛，并可生成电子证书页面。


七、常见问题
--------------------------------
1. 后端启动失败，提示数据库连接失败
检查 MySQL 是否启动、数据库 campus_carbon 是否存在，以及 application.yml 中的用户名和密码是否正确。

2. 前端请求失败
检查后端是否已经启动，并确认 uni-app项目/utils/request.js 中的 baseUrl 是否为 http://localhost:8080/api。

3. 8080 端口被占用
可以修改 src/main/resources/application.yml 中的 server.port，也可以关闭占用 8080 的程序。

4. AI 或 Agent 功能返回异常
检查 application.yml 中的大模型 api-key 和 endpoint 是否有效；如果涉及附近运动场地推荐，还需要检查高德地图 key 是否有效。

5. 小程序无法访问本地接口
微信开发者工具中可以临时关闭“校验合法域名”，或者确保本地调试配置允许访问 localhost。


八、项目目录说明
--------------------------------
src/main/java/com/campus/carbon
后端 Java 代码目录，包括 controller、service、mapper、model 等模块。

src/main/resources/application.yml
后端配置文件，包括端口、数据库、AI 和地图配置。

src/main/resources/static/admin/task-center.html
后台任务与活动配置相关页面。

sql
数据库建表脚本目录。

uni-app项目
前端小程序项目目录。

uni-app项目/pages
前端页面目录，包括登录、首页、步数、运动、排行、商城、Agent、个人中心等页面。

uni-app项目/utils/request.js
前端接口请求封装文件。
