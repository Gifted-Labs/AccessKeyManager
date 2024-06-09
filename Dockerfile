FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install -y openjdk-21-jdk maven

COPY..

RUN mvn clean package -Dskip Tests

FROM openjdk:21-slim
# WORKDIR /accesskeymanager
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT [ "java","-jar","app.jar" ],