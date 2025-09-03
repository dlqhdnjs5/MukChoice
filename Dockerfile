# Multi-stage build for Spring Boot
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
RUN gradle dependencies --no-daemon

COPY src ./src
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app


COPY --from=builder /app/build/libs/*.jar app.jar

RUN mkdir -p /app/config

ENV JAVA_OPTS="-Xmx512m -Xms256m -Dspring.profiles.active=prod"

VOLUME ["/app/config"]

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.config.additional-location=file:/app/config/"]
