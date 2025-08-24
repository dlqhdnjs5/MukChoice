@echo off
echo "=== MukChoice Backend Docker Build Script ==="

REM private.properties 파일 확인
if not exist "private.properties" (
    echo "Error: private.properties 파일이 없습니다!"
    echo "private.properties 파일을 프로젝트 루트에 생성해주세요."
    pause
    exit /b 1
)

REM Docker 이미지 빌드
echo "Backend Docker 이미지 빌드 중..."
docker build -t mukchoice-backend:latest .

if %ERRORLEVEL% neq 0 (
    echo "Docker 빌드 실패!"
    pause
    exit /b 1
)

echo "빌드 완료!"
echo ""
echo "실행 방법:"
echo "Backend 단독 실행: docker run -p 8080:8080 -v %cd%\private.properties:/app/config/private.properties mukchoice-backend:latest"
echo ""
pause
