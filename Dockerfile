FROM ubuntu:latest AS build

RUN apt-get update

RUN apt-get install -y openjdk-21-slim maven

# Use the official Java 21 image as the base image
#FROM openjdk:21-jdk-slim

# Install Maven
#RUN apt-get update && apt-get install -y maven

# Set the working directory
WORKDIR /app

# Copy the application code
COPY src ./src
COPY pom.xml .

COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Expose the port your application listens on
EXPOSE 8080

COPY --from=build /target/accesskeymanager-0.0.1-SNAPSHOT.jar app.jar

# Set the command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
