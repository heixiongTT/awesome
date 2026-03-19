FROM 192.168.141.1:5000/heixiongtt/oraclejre8:0.0.1
VOLUME /tmp
ADD awesome-web/target/awesome-web-0.0.1-SNAPSHOT.jar app.jar
ENV JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -javaagent:/opt/agent/skywalking-agent.jar -Dskywalking.agent.service_name=awesome -Dskywalking.collector.backend_service=skywalking-server:11800 -jar /app.jar ${SPRING_PROFILES_ACTIVE:+--spring.profiles.active=$SPRING_PROFILES_ACTIVE}"]
