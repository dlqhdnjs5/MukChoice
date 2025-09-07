# Multi-stage build for Full-stack application
FROM node:18-alpine AS frontend-builder

WORKDIR /app/frontend

# Copy frontend package files
COPY frontend/package.json frontend/pnpm-lock.yaml ./
RUN npm install -g pnpm && pnpm install

# Copy frontend source and build
COPY frontend/ ./

# Copy .env.production file if it exists (for production build)
COPY frontend/.env.production* ./

# Build in production mode
RUN pnpm build --mode production

# Backend build stage
FROM gradle:8.5-jdk21 AS backend-builder

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
RUN gradle dependencies --no-daemon

COPY src ./src

# Copy frontend build output to Spring Boot static resources
COPY --from=frontend-builder /app/frontend/dist ./src/main/resources/static

RUN gradle build -x test --no-daemon

# Final runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=backend-builder /app/build/libs/*.jar app.jar

RUN mkdir -p /app/config

ENV JAVA_OPTS="-Xmx512m -Xms512m -Dspring.profiles.active=prod"

VOLUME ["/app/config"]

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.config.additional-location=file:/app/config/"]
