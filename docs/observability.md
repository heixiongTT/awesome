# 可观测性基线规范

本文档对应 Linear 任务 **HEI-43：现代化日志、指标与链路追踪规范**，用于给当前 `awesome-web` 模块提供可直接落地的观测基线。

## 1. 目标

- **日志**：统一结构化字段，便于 ELK / Loki / OpenSearch 检索。
- **指标**：暴露应用与 HTTP 核心指标，为 Prometheus / Grafana 做接入准备。
- **链路追踪**：约定 `traceId` / `requestId` 的贯通规则，兼容现有 SkyWalking。
- **访问日志**：把每次请求都收敛到统一 access log，便于排障和审计。

## 2. 已落地内容

### 2.1 日志

- `logback.xml` 统一输出以下字段：
  - `app`
  - `traceId`
  - `requestId`
  - `thread`
  - `logger`
  - `message`
- 根日志负责应用日志。
- `ACCESS_LOG` 独立负责访问日志。
- `ERROR_FILE` 单独滚动保存错误日志。

### 2.2 requestId 贯通

`ObservabilityFilter` 在每个请求入口执行以下动作：

1. 优先读取请求头 `X-Request-Id`。
2. 如果上游未传入，则自动生成 UUID 去短横线后的值。
3. 写入 MDC 键 `requestId`。
4. 回写响应头 `X-Request-Id`。
5. 输出统一 access log。

这样可以保证：

- 前端、网关、服务端、日志平台可以围绕同一个 `requestId` 检索。
- 即便当前 tracing agent 不可用，也能依赖 `requestId` 做基础排障。

### 2.3 traceId 贯通

- 当前项目继续兼容 SkyWalking 注入的 `tid` MDC 值。
- 日志统一使用 `traceId=%X{tid:-NA}` 输出。
- 若未来升级到 Micrometer Tracing / OpenTelemetry，可保持日志字段名 `traceId` 不变，仅替换底层采集实现。

### 2.4 指标

已引入：

- `spring-boot-starter-actuator`
- `micrometer-registry-prometheus`

当前暴露端点：

- `/actuator/health`
- `/actuator/info`
- `/actuator/metrics`
- `/actuator/prometheus`

已设置基础指标标签：

- `application=${spring.application.name}`
- `environment=${spring.profiles.active}`

## 3. 日志分层规范

### 3.1 访问日志

用于记录每个 HTTP 请求，必须包含：

- `requestId`
- `traceId`
- `method`
- `path`
- `query`
- `status`
- `durationMs`
- `clientIp`
- `userAgent`

### 3.2 业务日志

用于记录关键状态变化，例如：

- 创建需求
- 更新状态
- 删除资源
- 远程调用失败

建议补充业务字段，例如：

- `bizCode`
- `operator`
- `resourceId`
- `result`

### 3.3 异常日志

异常日志只在真正失败时记录，避免与 access log 重复刷屏。建议固定包含：

- `requestId`
- `traceId`
- `exception`
- `errorCode`
- `message`

## 4. 指标接入建议

除 Actuator 默认指标外，后续应补充以下自定义指标：

- `awesome_requirement_created_total`
- `awesome_requirement_status_updated_total`
- `awesome_requirement_query_total`
- `awesome_remote_call_fail_total`
- `awesome_business_exception_total`

优先规则：

- **计数器**：记录调用次数、失败次数、重试次数。
- **计时器**：记录接口耗时、数据库查询耗时、远程调用耗时。
- **仪表盘**：避免高基数字段，不把 `requestId`、原始 URL 参数作为 label。

## 5. tracing 演进建议

当前版本较老，建议分两步走：

1. **短期**：保留 SkyWalking agent + 日志中的 `traceId/requestId` 规范。
2. **中期升级**：在 Spring Boot / Spring Cloud 主版本升级后，迁移到 Micrometer Tracing 或 OpenTelemetry。

迁移时建议保持以下字段语义不变：

- `traceId`：分布式链路 ID。
- `requestId`：单次入口请求 ID，由网关或服务入口生成。
- `spanId`：可选，在日志平台需要更细粒度关联时再开启。

## 6. Prometheus 抓取示例

```yaml
scrape_configs:
  - job_name: awesome-web
    metrics_path: /awesome/actuator/prometheus
    static_configs:
      - targets:
          - localhost:8080
```

> 注意：由于应用设置了 `server.servlet.context-path=/awesome`，抓取路径应包含该上下文。

## 7. 下一步建议

- 在 `GlobalExceptionHandler` 中统一补充 `requestId` / `traceId` 返回字段。
- 为核心 Service 增加 Micrometer `Counter` / `Timer`。
- 在网关层统一透传 `X-Request-Id`。
- 升级主版本后评估 OpenTelemetry exporter、OTLP collector、Grafana Tempo / Jaeger 接入。
