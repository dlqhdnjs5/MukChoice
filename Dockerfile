# Backend Only - Multi-stage build for Spring Boot application
FROM gradle:8.5-jdk21 AS build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 설정 파일들 복사 (캐시 최적화)
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle

# 의존성 다운로드 (캐시 레이어)
RUN gradle dependencies --no-daemon

# 소스 코드 복사
COPY src src

# private.properties 파일 복사 (빌드 시점에 포함)
COPY private.properties src/main/resources/private.properties

# 애플리케이션 빌드
RUN gradle clean bootJar --no-daemon

# Runtime stage
FROM openjdk:21-jre-slim

# 애플리케이션 사용자 생성 (Windows Docker Desktop에서도 호환)
RUN useradd -r -s /bin/false mukchoice

# 로그 디렉토리 생성 (log4j2 설정과 일치하도록 두 경로 모두 생성)
RUN mkdir -p /app/logs /mukchoice/logs && \
    chown -R mukchoice:mukchoice /app /mukchoice

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 볼륨 마운트 포인트 (로그와 설정 파일용)
VOLUME ["/app/config", "/app/logs", "/mukchoice/logs"]

# 사용자 전환
USER mukchoice

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행 (프로파일 기본값을 local로 설정)
ENTRYPOINT ["java", \
    "-Xms256m", \
    "-Xmx512m", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=200", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.config.additional-location=file:/app/config/", \
    "-Dspring.profiles.active=${PROFILE:-local}", \
    "-jar", \
    "app.jar"]
