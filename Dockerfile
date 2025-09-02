# Multi-stage build for Spring Boot
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

# Build the application
RUN gradle build -x test --no-daemon

FROM openjdk:21-jre-slim

WORKDIR /app

# Copy the built JAR
COPY --from=builder /app/build/libs/*.jar app.jar

# Create directory for external config
RUN mkdir -p /app/config

# Set JVM options for production
ENV JAVA_OPTS="-Xmx512m -Xms256m -Dspring.profiles.active=prod"

# External config file will be mounted here
VOLUME ["/app/config"]

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.config.additional-location=file:/app/config/"]
