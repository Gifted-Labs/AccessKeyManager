# Use the official Java 21 image as the base image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper script (if you're using it)
COPY mvnw .
COPY .mvn .mvn

# Copy the application code
COPY src ./src
COPY pom.xml .

# Build the application
RUN ./mvnw clean package -DskipTests

#COPY target/accesskeymanager-0.0.1-SNAPSHOT.jar /app/accesskeymanager-0.0.1-SNAPSHOT.jar


# Expose the port your application listens on
EXPOSE 8080

# Set the command to run the Spring Boot application
CMD ["java", "-jar", "target/*.jar"]
