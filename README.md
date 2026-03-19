# awesome

一个基于 **Java 8 + Spring Boot + Spring Data JPA** 的多模块脚手架工程，适合作为中小型后端服务的起点。

## 模块说明

- `awesome-dto`：对外 API、请求对象、DTO 定义。
- `awesome-web`：Web 层、Service 层、JPA 实体、Repository 与运行配置。

## 当前脚手架能力

- 基于 `Requirement` 示例演示完整的分层结构：`Controller -> Service -> Repository -> JPA Entity`。
- 提供统一的参数校验异常返回。
- 默认开发环境使用 H2 内存数据库，开箱即跑；生产环境可以平滑切换到 MySQL。
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


## OpenAPI / Swagger 文档

启动服务后，可通过以下地址访问自动生成的接口文档：

- Swagger UI：`http://localhost:8080/awesome/swagger-ui.html`
- Swagger JSON：`http://localhost:8080/awesome/v2/api-docs`

文档中已补充：

- `Requirement` 接口的操作说明。
- 请求/响应 schema。
- 常见错误码（`400` 参数校验失败、`404` 资源不存在）。
- 每个操作的请求示例参数。

## 启动方式

```bash
mvn clean test
mvn -pl awesome-web spring-boot:run
```

## 后续建议

- 增加 `BaseEntity`、审计字段与统一分页响应。
- 为 `Student` 等其他领域对象继续补齐 Repository 与 JPA 映射。
- 在生产环境中将 `application-prod.properties` 连接到 MySQL 并配合 Flyway/Liquibase 管理表结构。
