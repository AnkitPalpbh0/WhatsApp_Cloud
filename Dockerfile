# Use a JDK base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy build output (your JAR) into the container
COPY build/libs/*.jar app.jar

# Expose the port (change if your app runs on another port)
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
