# awesome

一个基于 **Java 8 + Spring Boot + Spring Data JPA** 的多模块脚手架工程，适合作为中小型后端服务的起点。

## 模块说明

- `awesome-dto`：对外 API、请求对象、DTO 定义。
- `awesome-web`：Web 层、Service 层、JPA 实体、Repository 与运行配置。

## 当前脚手架能力

- 基于 `Requirement` 示例演示完整的分层结构：`Controller -> Service -> Repository -> JPA Entity`。
- 提供统一的参数校验异常返回。
- 默认通过 `local / test / stage / prod` 四套 Profile 管理环境差异；本地默认使用 H2，测试/预发/生产通过环境变量注入外部依赖。
- 提供基础 CRUD 与按 `status` / `creator` 过滤查询示例。
- 测试环境内置集成测试，便于二次开发时快速回归。

## Requirement API 示例

### 创建需求

```bash
curl -X POST 'http://localhost:8080/awesome/requirements' \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "完善JPA脚手架",
    "description": "补齐Repository与测试",
    "priority": "HIGH",
    "creator": "codex"
  }'
```

### 查询需求列表

```bash
curl 'http://localhost:8080/awesome/requirements?creator=codex&status=TODO'
```

### 更新状态

```bash
curl -X PUT 'http://localhost:8080/awesome/requirements/status' \
  -H 'Content-Type: application/json' \
  -d '{
    "id": 1,
    "status": "DONE"
  }'
```

## 启动方式

### 本地开发

```bash
mvn clean test
mvn -pl awesome-web spring-boot:run -Dspring-boot.run.profiles=local
```

### 其他环境

```bash
java -jar awesome-web/target/awesome-web-0.0.1-SNAPSHOT.jar --spring.profiles.active=stage
SPRING_PROFILES_ACTIVE=prod java -jar awesome-web/target/awesome-web-0.0.1-SNAPSHOT.jar
```

> 不再在产物内硬编码 `spring.profiles.active`，请通过启动参数、环境变量或容器编排配置注入。

## 后续建议

- 增加 `BaseEntity`、审计字段与统一分页响应。
- 为 `Student` 等其他领域对象继续补齐 Repository 与 JPA 映射。
- 在测试、预发、生产环境中通过环境变量注入数据库与注册中心地址，并配合 Flyway/Liquibase 管理表结构。
