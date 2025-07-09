# Stage 1: Build the application
FROM gradle:8.2-jdk17-alpine AS builder

WORKDIR /app

# Copy everything needed to build the app
COPY . .

# Make gradlew executable
RUN chmod +x ./gradlew

# Run the Gradle build, skipping tests
RUN ./gradlew clean build -x test

# Stage 2: Create final runtime image
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"] 
