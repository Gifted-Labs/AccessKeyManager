# Use Maven to build the application in the first stage
FROM maven:3.8.4-openjdk-21 AS build
WORKDIR /accesskeymanager
COPY pom.xml /accesskeymanager
COPY src /accesskeymanager/src
RUN mvn clean package -DskipTests


FROM openjdk:21-jdk-slim
VOLUME /tmp
WORKDIR /accesskeymanager
COPY target/accesskeymanager-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT [ "java","-jar","app.jar" ]