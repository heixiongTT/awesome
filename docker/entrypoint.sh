#!/bin/sh
set -eu

JAVA_AGENT_OPTS=""
if [ "${ENABLE_SKYWALKING:-false}" = "true" ] && [ -f "${SW_AGENT_PATH}" ]; then
  JAVA_AGENT_OPTS="-javaagent:${SW_AGENT_PATH} -Dskywalking.agent.service_name=${SW_AGENT_NAME:-awesome} -Dskywalking.collector.backend_service=${SW_AGENT_COLLECTOR_BACKEND_SERVICES:-skywalking-server:11800}"
fi

exec sh -c "java ${JAVA_OPTS:-} ${JAVA_AGENT_OPTS} -Dserver.port=${APP_PORT:-8080} -jar /app/app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev}"
