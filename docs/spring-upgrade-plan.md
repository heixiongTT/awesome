# Spring Boot / Spring Cloud 主版本升级路径（HEI-36）

## 1. 当前基线

当前项目的父 POM 仍固定在以下组合：

- Java 17
- Spring Boot `2.1.6.RELEASE`
- Spring Cloud `Finchley.RELEASE`
- Spring Cloud Netflix Hystrix / Eureka / OpenFeign 旧版用法

这套版本组合可以运行示例代码，但已经明显落后于当前主流维护区间，后续继续保留会带来几个直接问题：

1. 依赖生态过旧，后续安全修复与社区支持都很弱。
2. 与现代 JDK、容器基础镜像、监控组件的兼容性越来越差。
3. Hystrix 已经不适合作为后续演进基础，直接阻碍 Spring Cloud 主版本升级。
4. 如果直接从当前版本跳到 Boot 3，代码中的 `javax.*`、旧 Netflix 注解与配置方式都会形成迁移阻塞。

## 2. 官方兼容性判断

结合 Spring 官方兼容信息，可以把升级路线拆成两条：

| 路线 | Java | Spring Boot | Spring Cloud | 结论 |
| --- | --- | --- | --- | --- |
| 稳妥路线 | 8 -> 11/17 | 2.1.6 -> 2.7.x | Finchley -> 2021.0.x | **推荐先执行**，改动面可控，可先清掉 Hystrix/旧配置债务 |
| 直接跨代路线 | 17 | 3.x | 2023.0.x（或后续与 Boot 3.3 对齐版本） | **可以评估，但不建议从当前仓库直接硬跳**，因为 Jakarta、Hystrix、配置加载方式都会同时变化 |

### 为什么先落到 Boot 2.7.x

Boot 2.7 是 Spring Boot 2 时代最后一个主线版本，适合作为从老旧 Spring Cloud Netflix 体系迁出的过渡台阶：

- 虽然仓库当前已经切到 Java 17，但仍可作为旧 Spring Boot 2 技术栈的收官升级点，降低一次性升级风险。
- 可以先把 `Hystrix`、旧版 OpenFeign、旧版 Config/Bootstrap 配置方式问题清掉。
- 为后续 `Java 17 + Spring Boot 3.x + Spring Cloud 2023.x` 打测试和兼容基础。

### 为什么暂不建议直接从当前仓库跳 Boot 3

当前代码里已经能看到几个 Boot 3 的典型阻塞项：

- DTO 与 Web 模块还在使用 `javax.validation.*`。
- JPA 实体仍使用 `javax.persistence.*`。
- `@EnableEurekaClient`、Hystrix 注解等旧式 Spring Cloud Netflix 写法仍在主流程里。
- `bootstrap.properties` 仍在使用旧版 Config Client 启动期配置习惯。

如果此时直接硬升 Boot 3，需要同时完成：

1. `javax.* -> jakarta.*` 包迁移。
2. Hystrix 全量移除并迁移到 Spring Cloud CircuitBreaker / Resilience4j。
3. 重新核对 Spring Cloud Config、Feign、Eureka 在新版本上的自动配置行为。
4. 把运行 JDK 一次性提升到 Java 17。

这些工作不是不能做，但对于当前这个仍带有脚手架/示例性质的仓库来说，风险高于收益。

## 3. 本仓库中的具体升级阻塞项

### 3.1 依赖与版本管理阻塞

1. 父 POM 直接固定到 `Spring Boot 2.1.6.RELEASE` 与 `Finchley.RELEASE`。
2. 子模块又显式写死了：
   - `spring-cloud-starter-openfeign:2.0.1.RELEASE`
   - `spring-cloud-starter-netflix-hystrix:2.0.0.RELEASE`
   - `spring-cloud-openfeign-core:2.1.3.RELEASE`
3. `awesome-web` 中使用了 `spring-boot-starter-config`，升级时需要重新确认是否本意是使用 Spring Cloud Config Client；如果是，应按目标 Spring Cloud 版本切换到对应 starter 与配置方式。

### 3.2 代码层阻塞

1. `StudentServiceImpl` 直接依赖 `@HystrixCommand`，而且 fallback 方法并未真正实现。
2. `AwesomeWebApplication` 使用 `@EnableEurekaClient`，升级后应评估是否仍有必要显式保留。
3. `awesome-dto` 与 `awesome-web` 广泛使用 `javax.validation.*`、`javax.persistence.*`，这会阻塞 Boot 3 / Spring Framework 6。
4. `ValidateApi` 仍是空接口，说明当前 Feign 契约层还有明显半成品痕迹，不适合在升级时继续放大复杂度。

### 3.3 配置与运行方式阻塞

1. `application.properties` 里硬编码 `spring.profiles.active=dev`，不利于升级过程中的多环境验证。
2. `bootstrap.properties` 仍保留老式 Spring Cloud Config 入口配置，需要结合目标 Spring Cloud 版本确认是否保留、迁移或删除。
3. 当前仓库缺少覆盖升级风险的测试分层，技术栈跨代时缺少可靠安全网。

## 4. 推荐执行方案

### 阶段 A：先完成 Boot 2.7.x / Cloud 2021.0.x 过渡

目标：把项目从“过老版本”拉回到“可持续维护”的 Spring Boot 2 终点。

建议顺序：

1. **先稳定构建链路**
   - 修复 Maven 仓库拉取问题。
   - 确保 `mvn test` 至少可以稳定运行。
2. **移除升级硬阻塞**
   - 下线 Hystrix。
   - 用 Spring Cloud CircuitBreaker + Resilience4j 替代，或者如果当前没有真实远程容错需求，就先删掉无效注解与依赖。
3. **统一依赖版本来源**
   - 删除子模块中手写的 Spring Cloud 具体版本号。
   - 全部交给父 POM 的 BOM 管理。
4. **升级到与 Java 17 兼容、且仍属于 Boot 2 时代的目标组合**
   - Spring Boot `2.7.x`
   - Spring Cloud `2021.0.x`
5. **补充回归测试**
   - 至少覆盖 Requirement 主链路、参数校验失败、删除/查询不存在资源、配置启动测试。

Boot 2.7 阶段的验收标准：

- 应用可在本地稳定启动。
- 测试可执行。
- Eureka / Feign / Config 的真实使用情况已经澄清。
- Hystrix 已不再出现在主分支依赖与代码中。

### 阶段 B：再进入 Boot 3.x / Cloud 2023.0.x

目标：进入当前可长期维护的主流 Spring 生态。

建议顺序：

1. JDK 升级到 Java 17。
2. 全仓 `javax.*` 切换到 `jakarta.*`。
3. 逐个核对：
   - JPA 实体与校验注解
   - Spring MVC / Validation 行为差异
   - Feign 客户端契约
   - 配置加载方式
4. 完成 Spring Boot 3.x 与 Spring Cloud 2023.0.x 的兼容验证。
5. 重新验证日志、监控、容器镜像和 CI。

Boot 3 阶段的验收标准：

- Java 17 运行稳定。
- 全量编译通过，仓库中不再出现业务代码级别的 `javax.persistence` / `javax.validation`。
- 不再依赖 Hystrix。
- 配置、服务发现、Feign 契约与测试在新版本下均通过。

## 5. 本次 issue 建议落地产出

这个 issue 当前更适合作为“升级路径设计 + 阻塞项盘点”里程碑，而不是一次性完成跨代升级。建议拆成以下子任务：

1. **HEI-35**：先移除 Hystrix，切到现代熔断方案。
2. **构建链路任务**：修复 Maven 仓库与 CI 可构建性。
3. **Boot 2.7 任务**：完成父 POM / Cloud BOM / Config Client / Feign 版本升级。
4. **Boot 2.7 收敛任务**：在已切换到 JDK 17 的基础上继续完成 Spring 依赖收敛。
5. **Boot 3 任务**：完成 Jakarta 迁移与 Spring Framework 6 兼容改造。

## 6. 结论

### 推荐结论

- **推荐主路径：先升级到 Spring Boot 2.7.x + Spring Cloud 2021.0.x，再进入 Spring Boot 3.x。**
- **不推荐当前仓库从 `2.1.6 + Finchley` 直接硬跳到 Boot 3.x。**

### 决策理由

1. 当前仓库存在明显的 Hystrix、`javax.*`、空 Feign 契约、旧 Config 习惯等技术债。
2. 这些问题在 Boot 2.7 阶段可以更低风险地逐步清理。
3. Boot 2.7 可以作为“技术债清扫站”，Boot 3 再作为“平台现代化收口阶段”。

## 7. 升级参考资料

- Spring Boot 3.3 系统要求（Java 17+）：https://docs.spring.io/spring-boot/system-requirements.html
- Spring Cloud Supported Versions（官方兼容矩阵）：https://github.com/spring-cloud/spring-cloud-release/wiki/Supported-Versions
- Spring Boot 3.0 Migration Guide：https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide
