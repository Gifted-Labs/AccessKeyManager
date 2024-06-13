# Use the official Java 21 image as the base image
FROM openjdk:21-jdk-slim

# Install Maven
RUN apt-get update && apt-get install -y maven

# Set the working directory
WORKDIR /app

# Copy the application code
COPY src ./src
COPY pom.xml .

# Build the application
RUN mvn clean package -DskipTests

# Copy the packaged JAR file
#COPY target/*.jar /app/*.jar

# Expose the port your application listens on
EXPOSE 8080

# Set the command to run the Spring Boot application
CMD ["java", "-jar", "target/*.jar"]