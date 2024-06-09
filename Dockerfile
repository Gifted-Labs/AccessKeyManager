FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install -y openjdk-21-jdk maven


RUN mvn clean package -Dskip Tests

FROM openjdk:21-slim

EXPOSE 8080
# WORKDIR /accesskeymanager

COPY --from=build /target/accesskeymanager-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT [ "java","-jar","app.jar" ]
