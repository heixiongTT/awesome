FROM 192.168.141.1:5000/heixiongtt/oraclejre8:0.0.1
VOLUME /tmp
ADD awesome-web/target/awesome-web-0.0.1-SNAPSHOT.jar app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]
ENTRYPOINT ["java","-javaagent:/opt/agent/skywalking-agent.jar","-Dskywalking.agent.service_name=awesome","-Dskywalking.collector.backend_service=skywalking-server:11800","-jar","/app.jar"]