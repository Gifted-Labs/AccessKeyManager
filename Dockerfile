FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install -y openjdk-21-jdk mave
COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:21-slim

COPY --from=build /target/accesskeymanager-0.0.1-SNAPSHOT.jar app.jar

CMD["java", "-jar", "app.jar"]