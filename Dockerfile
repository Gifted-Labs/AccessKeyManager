# Use the official Java 21 image as the base image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the application code
COPY . .

# Build the application
RUN ./mvnw clean package -DskipTests

# Copy the packaged JAR file
COPY target/*.jar /app/*.jar

# Expose the port your application listens on
EXPOSE 8080

# Set the command to run the Spring Boot application
CMD ["java", "-jar", "*.jar"]