# Java 17 升级评估

## 1. 结论摘要

当前仓库**不适合直接把 `java.version` 从 1.8 改到 17 后立即上线**，原因不是 Java 17 本身无法运行，而是项目依赖栈仍停留在 **Spring Boot 2.1.6.RELEASE + Spring Cloud Finchley.RELEASE + Hystrix + `javax.*` API** 的早期组合；这套组合与现代 Java 17 / Spring Boot 3 生态之间存在明显断层。

基于当前代码与依赖现状，建议采用下面的目标与节奏：

- **推荐目标版本**：`Java 17 + Spring Boot 3.x + Spring Cloud 2023.0.x/2024.0.x`。
- **推荐实施方式**：分两阶段推进，而不是一步到位硬切：
  1. **阶段 A：工程体检与阻塞项清理**（移除 Hystrix、梳理 Feign/Eureka/Config、修复构建链路、补测试）。
  2. **阶段 B：正式迁移到 Java 17 与 Spring Boot 3.x**（`javax` -> `jakarta`、容器镜像升级、配置与监控回归验证）。

如果团队希望**降低一次性改造风险**，也可以增加一个过渡阶段：

- **过渡路线**：先升到 `Spring Boot 2.7.x + Spring Cloud 2021.0.x`，稳定后再升到 `Java 17 + Spring Boot 3.x`。

## 2. 当前现状（仓库静态检查）

### 2.1 基线版本

- 父 POM 当前固定：
  - `java.version=1.8`
  - `spring.boot.version=2.1.6.RELEASE`
  - `spring.cloud.version=Finchley.RELEASE`
- 编译插件仍使用：
  - `<source>1.8</source>`
  - `<target>1.8</target>`
- Docker 基础镜像仍是私有 `oraclejre8`。

### 2.2 与升级相关的关键依赖

`awesome-web` 当前包含以下与 Java 17 / Boot 3 升级密切相关的历史依赖：

- `spring-cloud-starter-netflix-eureka-client`
- `spring-cloud-starter-openfeign:2.0.1.RELEASE`
- `spring-cloud-starter-netflix-hystrix:2.0.0.RELEASE`
- `spring-boot-starter-config`
- `spring-boot-starter-thymeleaf`
- `apm-toolkit-logback-1.x:6.4.0`

`awesome-dto` 中仍直接依赖：

- `javax.validation:validation-api`
- `spring-cloud-openfeign-core:2.1.3.RELEASE`

### 2.3 代码层面的升级信号

代码中已经出现会影响 Java 17 / Boot 3 迁移的典型特征：

- 广泛使用 `javax.validation.*`。
- JPA 实体使用 `javax.persistence.*`。
- 存在 `HystrixCommand` 注解与 Hystrix 依赖。
- `@EnableFeignClients` 仍建立在老版本 Spring Cloud 上。
- `spring.profiles.active=dev` 被写死在应用配置中。
- Docker 运行时仍假定 Java 8 目录与 agent 布局。

## 3. 为什么不能只升级 JDK

### 3.1 Spring 生态的主约束不在 JDK，而在框架代际

根据 Spring 官方系统要求，**Spring Boot 3.x 需要 Java 17 及以上**；而 Spring Cloud 官方兼容矩阵显示，当前受支持的 Spring Cloud 发布列车对应的是 **Spring Boot 3.x / 4.x** 生态，`Finchley.RELEASE` 已不在受支持范围内。

这意味着：

- 如果只升级到 Java 17，但仍停留在 `Boot 2.1.6 + Finchley`，虽然**部分场景可能勉强启动**，但并不是一条可维护、可验证、可持续的目标路线。
- 如果目标是“真正完成 Java 17 升级”，实际落点应是**连同 Spring Boot / Spring Cloud 主版本一起升级**。

### 3.2 Boot 3 迁移一定会触发 `javax` -> `jakarta`

Oracle 的 JDK 迁移文档说明，JDK 11 起移除了多项历史 Java EE 组件；而 Spring Framework 6 / Spring Boot 3 又全面转向 **Jakarta EE 9+** 命名空间。因此当前仓库中这些包名都需要重构：

- `javax.validation.*` -> `jakarta.validation.*`
- `javax.persistence.*` -> `jakarta.persistence.*`

这是本次升级最明确、最可预见的一类代码改造。

## 4. 兼容性评估

### 4.1 可以保留或低风险迁移的部分

以下内容从语法层面看，对 Java 17 迁移阻力较小：

- 常规 Spring MVC / JPA CRUD 代码。
- Lombok 基础注解用法。
- H2 / MySQL 这种基础数据库接入方式。
- 简单 DTO / Repository / Service 分层结构。

### 4.2 高风险或必须处理的部分

#### A. Hystrix

- Hystrix 已是历史技术栈，不适合作为 Java 17 / 新版 Spring Cloud 目标架构的一部分。
- 当前代码中存在 `@HystrixCommand`，而且示例实现本身也不完整。
- 建议：**在 Java 17 升级前先移除 Hystrix 或迁移到 Resilience4j / Spring Cloud CircuitBreaker**。

#### B. `javax.*` 命名空间

- `awesome-dto` 的校验注解与 `awesome-web` 的 JPA/Validation 导入都要改。
- 这类改动虽然机械，但覆盖面广，容易带来编译错误与序列化/校验行为变化。

#### C. Spring Cloud Finchley / OpenFeign 老版本

- 当前 Feign 相关依赖版本分散，且直接在子模块中手动写死版本。
- 升级时应尽量改成由 BOM 统一管理，避免版本错配。

#### D. 配置中心 / 注册中心依赖是否真的需要

- 项目已经引入 Config、Eureka、Feign，但从仓库代码量看更像“脚手架预置”。
- 如果这些能力没有实际业务依赖，升级前应先清理；否则每一项都会增加版本迁移成本。

#### E. SkyWalking Logback Toolkit

- 当前是 `6.4.0` 的老版本，需要单独核查其对 Logback、Spring Boot 3、JDK 17 的兼容性。
- 该项建议在正式升级时与运维/观测平台一起联调验证。

#### F. Docker/JRE 基础镜像

- 当前镜像仍使用私有 `oraclejre8`。
- 升级后必须同步切换到 Java 17 运行时镜像，例如企业基线镜像、Eclipse Temurin 或 Amazon Corretto 的 17 LTS 版本。

## 5. 推荐升级路线

## 路线 1：稳妥两跳（推荐）

### 第一步：先收敛到较新的 Boot 2 体系

目标：`Spring Boot 2.7.x + Spring Cloud 2021.0.x`

适用场景：

- 团队希望先完成依赖收敛和构建稳定化。
- 需要给测试、CI、配置治理留出缓冲时间。
- 希望把“框架升级”和“Jakarta 命名空间迁移”拆成两个阶段。

本阶段重点：

- 移除 Hystrix。
- 统一 Feign / Cloud 版本管理。
- 修复 Maven 仓库与 CI 构建。
- 增补回归测试。
- 清理无效依赖（例如不再使用的 Thymeleaf、Config、Eureka）。

### 第二步：再切到 Java 17 + Boot 3

目标：`Java 17 + Spring Boot 3.x + Spring Cloud 2023.0.x/2024.0.x`

本阶段重点：

- 全量迁移 `javax.*` -> `jakarta.*`。
- 升级插件、镜像、监控依赖。
- 验证序列化、参数校验、JPA 映射、Feign 调用和启动参数。

## 路线 2：直接升级到 Java 17 + Boot 3（激进）

适用场景：

- 项目业务简单、外部依赖少。
- 团队可以接受集中改造与较高的一次性回归成本。
- 能够较快删除 Hystrix 和历史云组件。

优点：

- 少一次中间态维护成本。
- 更快进入受支持的长期版本组合。

缺点：

- 回归压力更大。
- 需要一次处理 `jakarta`、依赖收敛、镜像、配置、观测等多项问题。

## 6. 预计改造清单

### 6.1 POM 与构建链路

- 将 `java.version` 提升到 `17`。
- 使用 `<maven.compiler.release>17</maven.compiler.release>` 替代 `source/target`。
- 升级 `maven-compiler-plugin`、`surefire`、`failsafe` 等构建插件到现代版本。
- 统一由父 POM/BOM 管理 Spring Boot / Spring Cloud / Feign 版本。
- 去掉模块内手动写死的旧版依赖版本号。

### 6.2 代码改造

- 所有 `javax.validation.*` 改为 `jakarta.validation.*`。
- 所有 `javax.persistence.*` 改为 `jakarta.persistence.*`。
- 检查是否还存在 `javax.servlet.*`、`javax.annotation.*` 等历史包。
- 去除 Hystrix 注解与降级逻辑，替换为现代熔断方案或先删除。

### 6.3 运行时与部署

- Dockerfile 基础镜像升级到 Java 17 LTS。
- 校验 SkyWalking agent 版本与启动参数是否兼容 Java 17。
- 校验容器内时区、编码、JVM 参数模板是否仍适用。

### 6.4 配置与环境治理

- 去掉 `application.properties` 中硬编码的 `spring.profiles.active=dev`。
- 区分本地开发、测试、预发、生产环境参数。
- 若不再使用 Spring Cloud Config / Eureka，应同步移除配置与依赖。

### 6.5 测试补强

至少补齐：

- Controller 层参数校验失败用例。
- Repository 边界查询用例。
- Service 规则与异常分支用例。
- 启动测试与关键 CRUD 回归。
- 若保留 Feign/注册中心，增加最小契约验证。

## 7. 风险点清单

### 高风险

- `javax` 到 `jakarta` 切换导致编译失败。
- Hystrix/Feign/Eureka 等历史云组件与新版 Spring Cloud 不兼容。
- SkyWalking / Logback / agent 版本与 Java 17 不匹配。
- Docker 运行时镜像未同步升级导致部署失败。

### 中风险

- 参数校验、异常处理、JSON 序列化行为存在细微变化。
- H2/MySQL 驱动版本升级后 SQL 方言或关键字行为变化。
- 测试覆盖不足导致升级后回归缺口。

### 低风险

- 业务 CRUD 结构简单，核心控制器/Service 模式本身不复杂。
- 模块数量不多，整体改造范围可控。

## 8. 建议的验收标准

完成 Java 17 升级不应只看“能编过”，建议以下标准全部通过：

1. **本地构建通过**：`mvn clean test` 成功。
2. **JDK 17 运行通过**：应用在 Java 17 容器中正常启动。
3. **核心接口回归通过**：Requirement 的增删改查正常。
4. **配置治理通过**：不同 profile 通过环境注入切换，而不是写死。
5. **部署链路通过**：Docker 镜像构建、启动、健康检查通过。
6. **观测链路通过**：日志、trace、agent 不报兼容性错误。
7. **遗留组件清理完成**：Hystrix 等不再阻塞后续 Spring Cloud 升级。

## 9. 建议排期

### Sprint 1：升级前置清理

- 修复 Maven 仓库与 CI。
- 清理 Student/Hystrix 等半成品代码。
- 识别并删除不再需要的 Cloud 依赖。
- 补充测试与 README/开发说明。

### Sprint 2：框架迁移准备

- 升级到 Boot 2.7.x（如果采用两跳方案）。
- 收敛依赖管理与插件版本。
- 完成镜像、配置与测试基线标准化。

### Sprint 3：Java 17 / Boot 3 正式迁移

- 迁移 `javax` -> `jakarta`。
- 升级 Spring Boot / Spring Cloud。
- 替换或移除 Hystrix。
- 联调日志、注册中心、配置中心、Feign。

### Sprint 4：灰度与生产验证

- 执行回归测试。
- 在测试环境做启动、压测、观测验证。
- 完成灰度发布与回滚预案。

## 10. 外部资料（官方）

以下资料适合作为后续实施时的版本判断依据：

- Spring Boot System Requirements：<https://docs.spring.io/spring-boot/system-requirements.html>
- Spring Cloud Supported Versions：<https://github.com/spring-cloud/spring-cloud-release/wiki/Supported-Versions>
- Oracle JDK 17 Migration Guide：<https://docs.oracle.com/en/java/javase/17/migrate/>

