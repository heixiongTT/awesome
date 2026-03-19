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

## 构建链路与 Maven 仓库约定

项目现在默认通过 `.mvn/maven.config` 强制使用仓库内的 `.mvn/settings.xml`，避免开发机 `~/.m2/settings.xml` 中遗留的代理或镜像配置污染当前构建。

### 本地开发（默认）

适用于可以直接访问公共 Maven 仓库的环境：

```bash
mvn clean test
mvn -pl awesome-web spring-boot:run
```

### 公司网络 / CI（推荐统一私服）

如果公司网络必须通过 Nexus / Artifactory 访问依赖，请按下面步骤配置：

1. 复制示例配置：
   ```bash
   cp .mvn/settings.corporate-example.xml ~/.m2/settings.xml
   ```
2. 将 `https://nexus.example.com/repository/maven-public/` 替换为公司的 Maven 私服地址。
3. 如私服需要鉴权，注入 `MAVEN_REPO_USERNAME` / `MAVEN_REPO_PASSWORD`。
4. 如必须走 HTTP/HTTPS 代理，请在 `proxies` 节点中显式配置，并把公司私服加入 `nonProxyHosts`，避免再次出现 `403 Forbidden`。

### CI 约定

仓库内新增了 `.github/workflows/maven.yml`：

- 优先读取 `MAVEN_SETTINGS_XML` Secret 作为 CI 专用 `settings.xml`。
- 未配置 Secret 时，回退到仓库内的 `.mvn/settings.xml`。
- 统一执行 `mvn test`，保证本地与 CI 的入口一致。

### 构建前置要求

- JDK：`8`
- Maven：`3.8+`（本地验证使用 `3.9.10`）
- 默认测试数据库：H2（测试/开发环境）
- 如果 `~/.m2/settings.xml` 中存在代理或镜像，请优先检查是否误拦截了 `repo.maven.apache.org` 或公司私服

## 启动方式

```bash
mvn clean test
mvn -pl awesome-web spring-boot:run
```

## 后续建议

- 增加 `BaseEntity`、审计字段与统一分页响应。
- 为 `Student` 等其他领域对象继续补齐 Repository 与 JPA 映射。
- 在生产环境中将 `application-prod.properties` 连接到 MySQL 并配合 Flyway/Liquibase 管理表结构。
