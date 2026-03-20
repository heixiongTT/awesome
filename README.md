# awesome

一个基于 **Java 17 + Spring Boot + Spring Data JPA** 的多模块脚手架工程，适合作为中小型后端服务的起点。

## 模块说明

- `awesome-dto`：对外 API、请求对象、DTO 定义。
- `awesome-web`：Web 层、Service 层、JPA 实体、Repository 与运行配置。

## 当前脚手架能力

- 基于 `Requirement` 示例演示完整的分层结构：`Controller -> Service -> Repository -> JPA Entity`。
- 提供统一的参数校验异常返回。
- 默认开发环境使用 H2 内存数据库，开箱即跑；生产环境可以平滑切换到 MySQL。
- 提供基础 CRUD 与按 `status` / `creator` 过滤查询示例。
- 新增基于 Binance 公共行情接口的市场数据接入流水线，支持抓取、标准化入库与查询最近行情记录。
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


## Market Data API 示例

### 抓取并入库

```bash
curl -X POST 'http://localhost:8080/awesome/market-data/ingestions' \
  -H 'Content-Type: application/json' \
  -d '{
    "source": "BINANCE",
    "symbol": "BTCUSDT",
    "interval": "1m",
    "limit": 100
  }'
```

### 查询最近行情

```bash
curl 'http://localhost:8080/awesome/market-data?source=BINANCE&symbol=BTCUSDT&interval=1m&limit=50'
```

## 启动方式

```bash
mvn clean test
mvn -pl awesome-web spring-boot:run
```

## 容器化与部署

### 构建镜像

```bash
docker build -t awesome-web:local .
```

### 运行容器

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=25.0" \
  awesome-web:local
```

### 健康检查

```bash
curl http://localhost:8080/awesome/actuator/health
```

- Docker 镜像默认启用容器友好的 JVM 参数，并通过 `JAVA_OPTS` 覆盖。
- 通过 `ENABLE_SKYWALKING=true`、`SW_AGENT_PATH`、`SW_AGENT_NAME` 与 `SW_AGENT_COLLECTOR_BACKEND_SERVICES` 可按需挂载 SkyWalking agent。
- Kubernetes 示例模板位于 `deploy/k8s/deployment.yaml`，包含 `readinessProbe`、`livenessProbe`、`startupProbe` 与基础资源限制。

## 后续建议

- 增加 `BaseEntity`、审计字段与统一分页响应。
- 为 `Student` 等其他领域对象继续补齐 Repository 与 JPA 映射。
- 在生产环境中将 `application-prod.properties` 连接到 MySQL 并配合 Flyway/Liquibase 管理表结构。
