# awesome

一个基于 **Java 8 + Spring Boot 2.1.6.RELEASE + Spring Cloud Finchley.RELEASE** 的多模块后端脚手架工程，当前用于演示 `Requirement` 需求管理场景，并保留了 Eureka、OpenFeign、Hystrix、JPA、H2/MySQL 等基础集成能力。

> 这是一个偏“老版本 Spring Cloud 技术栈”的示例工程。新成员接手时，建议先按本文档完成 **JDK / Maven / 仓库配置 / 本地依赖组件** 初始化，再进行功能开发。

## 目录结构

```text
awesome
├── awesome-dto   # 对外 API、请求对象、DTO 定义
├── awesome-web   # Web / Service / Repository / Entity / 配置
├── pom.xml       # 聚合工程与版本管理
└── Dockerfile    # 基于 JAR 的容器启动示例
```

## 技术栈与版本基线

| 项目 | 当前版本 / 说明 |
| --- | --- |
| JDK | Java 8 |
| Maven | 建议 Maven 3.6+ |
| Spring Boot | 2.1.6.RELEASE |
| Spring Cloud | Finchley.RELEASE |
| 数据访问 | Spring Data JPA |
| 服务发现 | Eureka Client |
| 远程调用 | OpenFeign |
| 熔断 | Hystrix |
| 数据库 | 本地默认 H2，生产可切换 MySQL |
| 监控埋点 | SkyWalking Java Agent（容器启动示例中提供） |

## 模块说明

### `awesome-dto`

负责沉淀跨模块共享的接口契约与数据模型：

- `api/`：对外 API 接口定义。
- `req/`：请求对象。
- `dto/`：返回 DTO。

### `awesome-web`

负责具体应用实现：

- `web/`：Controller 层。
- `service/`：业务服务层。
- `repository/`：JPA Repository。
- `domain/`：JPA Entity。
- `src/main/resources/`：运行配置。

## 当前已提供的示例能力

- `Requirement` 领域的基础 CRUD 流程。
- 参数校验失败的统一异常处理。
- 基于 H2 的本地开发环境，默认无需额外安装数据库即可运行。
- 基于 H2 + MockMvc 的集成测试示例。
- 容器启动示例与 SkyWalking Agent 接入示例。

## 开发环境准备

### 1. 安装 JDK

请安装 **JDK 8**，并确保以下命令可用：

```bash
java -version
javac -version
```

### 2. 安装 Maven

建议使用 **Maven 3.6 或更高版本**，并验证：

```bash
mvn -version
```

建议 Maven 运行时使用与项目一致的 JDK 8。

### 3. 配置 Maven 仓库

该项目依赖 Spring Boot / Spring Cloud 较老版本，在部分网络环境下直接访问 Maven Central 可能失败。团队应统一使用公司私服（如 Nexus / Artifactory）或可用镜像源。

可参考如下 `~/.m2/settings.xml` 结构，将仓库地址替换为你所在团队的实际地址：

```xml
<settings>
  <mirrors>
    <mirror>
      <id>company-maven</id>
      <mirrorOf>*</mirrorOf>
      <name>company maven mirror</name>
      <url>https://your-maven-mirror/repository/maven-public/</url>
    </mirror>
  </mirrors>

  <profiles>
    <profile>
      <id>company</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>false</enabled></snapshots>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>false</enabled></snapshots>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>

  <activeProfiles>
    <activeProfile>company</activeProfile>
  </activeProfiles>
</settings>
```

如果公司内网对公网仓库有限制，请优先让 CI、本地开发机、IDEA 使用同一份仓库策略，避免“本地能跑 / CI 失败”或“CI 能跑 / 本地失败”的情况。

### 4. IDE 建议

推荐使用 IntelliJ IDEA，并确认：

- Project SDK 指向 JDK 8。
- Maven Importer / Runner 使用 JDK 8。
- 打开 Lombok 插件并启用 Annotation Processing。

- 基于 `Requirement` 示例演示完整的分层结构：`Controller -> Service -> Repository -> JPA Entity`。
- 提供统一的参数校验异常返回。
- 默认通过 `local / test / stage / prod` 四套 Profile 管理环境差异；本地默认使用 H2，测试/预发/生产通过环境变量注入外部依赖。
- 默认开发环境使用 H2 内存数据库，开箱即跑；生产环境可以平滑切换到 MySQL。
- 已引入 Flyway 进行版本化数据库迁移，统一管理 H2 / MySQL 的表结构变更。
- 提供基础 CRUD 与按 `status` / `creator` 过滤查询示例。
- 测试环境内置集成测试，便于二次开发时快速回归。

## 配置文件说明

### `application.properties`

公共配置：

- 默认激活 `dev` profile。
- 应用 context-path 为 `/awesome`。
- 关闭 `open-in-view`。

### `application-dev.properties`

本地开发默认配置：

- 应用名：`awesome`
- Eureka 地址：`http://localhost:8761/eureka/`
- 默认启用 Eureka 注册与拉取注册表
- 数据源：H2 内存数据库
- `ddl-auto=update`
- 启用 H2 Console

### `application-test.properties`

测试配置：

- 不启用 Eureka / 服务发现
- 使用独立 H2 内存库
- `ddl-auto=create-drop`
- `server.servlet.context-path=`，便于测试直接访问 `/requirements`

### `bootstrap.properties`

保留了 Spring Cloud Config 的基础配置项：

- `spring.cloud.config.allow-override=true`
- `spring.cloud.config.label=profile`

当前仓库中未包含独立 Config Server，若本地不接配置中心，可直接使用本地 `application-*.properties` 运行。

## 本地依赖组件说明

### 必选依赖

如果仅调试 `Requirement` 示例接口，**默认只需要 JDK + Maven**，因为开发环境使用 H2 内存数据库。

### 可选依赖

#### 1. Eureka

`dev` 环境默认会向 `http://localhost:8761/eureka/` 注册并拉取注册表。

如果你本地 **没有启动 Eureka**，推荐使用以下任一方式禁用：

```bash
mvn -pl awesome-web spring-boot:run \
  -Dspring-boot.run.profiles=dev \
  -Dspring-boot.run.arguments="--eureka.client.enabled=false --spring.cloud.discovery.enabled=false"
```

或者直接启动打包后的 JAR：

```bash
java -jar awesome-web/target/awesome-web-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=dev \
  --eureka.client.enabled=false \
  --spring.cloud.discovery.enabled=false
```

#### 2. MySQL

若你希望切换到 MySQL，请额外提供：

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.datasource.driver-class-name=com.mysql.jdbc.Driver`（当前依赖版本下）

建议新增独立的 `application-local-mysql.properties` 或 `application-prod.properties`，不要直接覆盖 H2 配置。

#### 3. SkyWalking

`Dockerfile` 中演示了通过 `-javaagent:/opt/agent/skywalking-agent.jar` 注入 SkyWalking Agent 的方式。

如果你本地没有 SkyWalking 环境，不影响直接使用 Maven 或 JAR 本地启动应用。

## 启动方式

### 1. 拉取依赖并执行测试

```bash
mvn clean test
```

### 2. 本地运行（默认 dev 配置）

如果你已经准备好本地 Eureka：

```bash
mvn -pl awesome-web spring-boot:run
```

如果你只想快速启动接口、不依赖 Eureka：

```bash
mvn -pl awesome-web spring-boot:run \
  -Dspring-boot.run.arguments="--eureka.client.enabled=false --spring.cloud.discovery.enabled=false"
```

### 3. 打包运行

```bash
mvn clean package
java -jar awesome-web/target/awesome-web-0.0.1-SNAPSHOT.jar \
  --eureka.client.enabled=false \
  --spring.cloud.discovery.enabled=false
```

### 4. 访问地址

应用默认端口为 `8080`，context-path 为 `/awesome`，因此接口根路径为：

```text
http://localhost:8080/awesome
```

H2 Console 默认地址：

```text
http://localhost:8080/awesome/h2-console
```

> 说明：H2 Console 是否能访问，取决于 Spring Boot 2.1 的默认安全与 servlet 配置；如访问受限，请优先检查运行 profile 与上下文路径。

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

### 查询单个需求

```bash
curl 'http://localhost:8080/awesome/requirements/1'
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

## 数据库变更管理

- Flyway 脚本目录：`awesome-web/src/main/resources/db/migration`。
- 当前初始版本脚本：`V1__create_requirements_table.sql`，用于创建 `requirements` 表及基础索引。
- 已将 `spring.jpa.hibernate.ddl-auto` 从自动更新调整为 `validate`，启动时由 Flyway 先执行迁移，再由 Hibernate 校验实体与表结构是否一致。
- 如果后续需要新增字段或索引，请追加新的 `V{n}__description.sql`，不要直接依赖 Hibernate 自动改表。

## CI 验证流程

- 仓库已增加 GitHub Actions 工作流：`.github/workflows/ci.yml`。
- 每次向 `main` / `master` 推送，或发起 Pull Request 时，会执行以下最小校验：
  1. 使用 Temurin JDK 8 与 Maven 缓存恢复构建环境。
  2. 执行 `mvn -B -ntp -U dependency:go-offline` 预解析依赖。
  3. 执行 `mvn -B -ntp validate`，通过 Maven Enforcer 校验 JDK / Maven 基线。
  4. 执行 `mvn -B -ntp verify` 完成编译、测试与打包验证。

### 本地执行同款检查

```bash
mvn -B -ntp -U dependency:go-offline
mvn -B -ntp validate
mvn -B -ntp verify
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
curl -X DELETE 'http://localhost:8080/awesome/requirements/1'
```

## 本地开发建议流程

1. 按本文档完成 JDK、Maven、私服/镜像配置。
2. 先执行 `mvn clean test`，确认依赖和测试可跑通。
3. 初次调试推荐先禁用 Eureka，保证接口能快速启动。
4. 使用 H2 完成功能开发与自测。
5. 如需联调注册中心、配置中心、MySQL，再逐步切换到对应环境配置。

## 常见问题

### 1. `mvn clean test` 依赖下载失败

优先检查：

- `~/.m2/settings.xml` 是否已切到可用镜像/私服。
- 公司网络是否限制直连公网仓库。
- IDEA 与命令行 Maven 是否使用同一份 settings。
- 本机 Maven 是否实际使用 JDK 8。

### 2. 启动时尝试连接 Eureka 失败

这是因为 `dev` 环境默认开启了 Eureka Client。若本地没有 Eureka，请按前文命令增加：

- `--eureka.client.enabled=false`
- `--spring.cloud.discovery.enabled=false`

### 3. 为什么本地不需要 MySQL

因为 `application-dev.properties` 默认使用的是 H2 内存数据库，适合脚手架开发与快速验证。

### 4. 为什么测试接口路径没有 `/awesome`

测试 profile 中显式将 `server.servlet.context-path` 置空，因此 `MockMvc` 测试直接访问 `/requirements`。

## 后续建议

- 增加 `BaseEntity`、审计字段与统一分页响应。
- 为 `Student` 等其他领域对象继续补齐 Repository 与 JPA 映射。
- 在测试、预发、生产环境中通过环境变量注入数据库与注册中心地址，并配合 Flyway/Liquibase 管理表结构。
- 新增领域能力时，优先按 `Requirement` 示例补齐完整的 `Controller -> Service -> Repository -> Entity` 链路，再合入主干。
- 在生产环境中将 `application-prod.properties` 连接到 MySQL 并配合 Flyway/Liquibase 管理表结构。
- 增加 `application-local.properties`，避免默认 `dev` 配置对 Eureka 的硬依赖。
- 补充统一的公司 Maven 私服地址与账号申请流程。
- 增加 `application-prod.properties` 与 MySQL/Flyway 示例配置。
- 为本地联调补充 Docker Compose（如 Eureka、MySQL、SkyWalking）。
