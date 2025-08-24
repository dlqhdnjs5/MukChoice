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

# 애플리케이션 사용자 생성
RUN useradd -r -s /bin/false mukchoice

# 로그 디렉토리 생성 (절대 경로 사용)
RUN mkdir -p /mukchoice/logs && \
    chown -R mukchoice:mukchoice /mukchoice

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# private.properties를 위한 볼륨 마운트 포인트
VOLUME ["/app/config"]

# 사용자 전환
USER mukchoice

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.config.additional-location=file:/app/config/", \
    "-Dspring.profiles.active=${PROFILE:-local}", \
    "-jar", \
    "app.jar"]
