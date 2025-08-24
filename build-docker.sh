#!/bin/bash

echo "=== MukChoice Backend Docker Build Script ==="

# 서버의 절대 경로에서 private.properties 파일 확인
PRIVATE_PROPERTIES_PATH="/etc/mukchoice/private.properties"

if [ ! -f "$PRIVATE_PROPERTIES_PATH" ]; then
    echo "Error: private.properties 파일이 없습니다!"
    echo "private.properties 파일을 $PRIVATE_PROPERTIES_PATH 에 생성해주세요."
    exit 1
fi

# Docker 이미지 빌드 (서버의 설정 파일을 이미지에 포함)
echo "Backend Docker 이미지 빌드 중..."
cp $PRIVATE_PROPERTIES_PATH ./private.properties
docker build -t mukchoice-backend:latest .
rm ./private.properties  # 빌드 후 임시 파일 삭제

if [ $? -ne 0 ]; then
    echo "Docker 빌드 실패!"
    exit 1
fi

echo "빌드 완료!"
echo ""
echo "실행 방법:"
echo "Backend 단독 실행: docker run -p 8080:8080 mukchoice-backend:latest"
echo ""
echo "Backend API 확인: http://localhost:8080"
echo "헬스체크: http://localhost:8080/actuator/health"
echo ""
