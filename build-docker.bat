@echo off
echo "=== MukChoice Backend Docker Build Script ==="

REM Docker Hub 사용자명 설정 (변경 필요)
set DOCKER_USERNAME=dlqhdnjs5
set IMAGE_NAME=mukchoice-backend
set VERSION=latest

REM private.properties 파일 확인
if not exist "private.properties" (
    echo "Error: private.properties 파일이 없습니다!"
    echo "private.properties 파일을 프로젝트 루트에 생성해주세요."
    pause
    exit /b 1
)

REM Docker 이미지 빌드
echo "Backend Docker 이미지 빌드 중..."
docker build -t %IMAGE_NAME%:%VERSION% .
docker tag %IMAGE_NAME%:%VERSION% %DOCKER_USERNAME%/%IMAGE_NAME%:%VERSION%

if %ERRORLEVEL% neq 0 (
    echo "Docker 빌드 실패!"
    pause
    exit /b 1
)

echo "빌드 완료!"

REM Docker Hub 푸시 여부 확인
set /p PUSH_TO_HUB="Docker Hub에 푸시하시겠습니까? (y/n): "
if /i "%PUSH_TO_HUB%"=="y" (
    echo "Docker Hub에 로그인 중..."
    docker login
    if %ERRORLEVEL% neq 0 (
        echo "Docker Hub 로그인 실패!"
        pause
        exit /b 1
    )

    echo "Docker Hub에 이미지 푸시 중..."
    docker push %DOCKER_USERNAME%/%IMAGE_NAME%:%VERSION%
    if %ERRORLEVEL% neq 0 (
        echo "Docker Hub 푸시 실패!"
        pause
        exit /b 1
    )
    echo "Docker Hub 푸시 완료!"
    echo "이미지: %DOCKER_USERNAME%/%IMAGE_NAME%:%VERSION%"
)

echo ""
echo "실행 방법:"
echo "1. 로컬 프로파일로 실행:"
echo "   docker run -p 8080:8080 -e PROFILE=local -v %cd%\logs:/mukchoice/logs %IMAGE_NAME%:%VERSION%"
echo ""
echo "2. 개발 프로파일로 실행 (기본):"
echo "   docker run -p 8080:8080 -v %cd%\logs:/app/logs %IMAGE_NAME%:%VERSION%"
echo ""
echo "3. 설정 파일 별도 마운트:"
echo "   docker run -p 8080:8080 -v %cd%\private.properties:/app/config/private.properties %IMAGE_NAME%:%VERSION%"
echo ""
echo "4. Hub에서 실행:"
echo "   docker run -p 8080:8080 %DOCKER_USERNAME%/%IMAGE_NAME%:%VERSION%"
echo ""
pause
