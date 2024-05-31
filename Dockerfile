

FROM openjdk:21-jdk-slim
# WORKDIR /accesskeymanager
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT [ "java","-jar","app.jar" ]