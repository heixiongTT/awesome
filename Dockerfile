# syntax=docker/dockerfile:1.7

FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build

COPY pom.xml ./
COPY awesome-dto/pom.xml awesome-dto/pom.xml
COPY awesome-web/pom.xml awesome-web/pom.xml
RUN mvn -B -q -pl awesome-web -am dependency:go-offline

COPY awesome-dto/src awesome-dto/src
COPY awesome-web/src awesome-web/src
RUN mvn -B -pl awesome-web -am clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system spring \
    && useradd --system --gid spring --create-home spring

ENV APP_HOME=/app \
    APP_PORT=8080 \
    SPRING_PROFILES_ACTIVE=dev \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=25.0 -Djava.security.egd=file:/dev/./urandom" \
    ENABLE_SKYWALKING=false \
    SW_AGENT_NAME=awesome \
    SW_AGENT_COLLECTOR_BACKEND_SERVICES=skywalking-server:11800 \
    SW_AGENT_PATH=/opt/agent/skywalking-agent.jar

COPY --from=builder /build/awesome-web/target/awesome-web-0.0.1-SNAPSHOT.jar /app/app.jar
COPY docker/entrypoint.sh /app/entrypoint.sh

RUN chmod +x /app/entrypoint.sh \
    && chown -R spring:spring /app

USER spring
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl --fail --silent http://127.0.0.1:${APP_PORT}/awesome/actuator/health || exit 1

ENTRYPOINT ["/app/entrypoint.sh"]
