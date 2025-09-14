
<img width="512" height="512" alt="mukchoice_icon" src="https://github.com/user-attachments/assets/8edf837b-93cc-4469-821b-0678d7e6c137" />


# 먹초이스 - 위치 기반 맛집 추천 서비스
https://mukchoice.kr

2025.06.01~ 2025.09.14

프로젝트 인원: 이보원

> **“내 위치와 그룹 친구들과 함께, 랜덤으로 즐기는 맛집 선택의 재미”**
> 
> 점심시간, 데이트, 친구와의 자리에서 뭘먹을지 고민될땐 먹초이스에게 선택을 맡겨보세요!
 

---

## 프로젝트 개요
**Mukchoice**는 위치 기반으로 맛집을 추천하고, 그룹 관리와 랜덤 추천 기능을 통해 즐겁게 의사결정을 지원하는 서비스입니다.  
프론트엔드와 백엔드를 완전히 분리하고, Docker 기반 배포 파이프라인을 구축하여 운영 환경에서도 안정성을 확보했습니다.  

---

## 기능 소개
### 로그인

<img width="500" height="300" alt="image" src="https://github.com/user-attachments/assets/e3a11b57-6e6e-4bb1-8cd7-e45b0c43826c" />

### 홈

**검색결과, 카테고리별 랜덤 초이스**

<img src="https://github.com/user-attachments/assets/7ecdbb55-ab8f-4c98-b128-609373973484" width="300" height="500" />
<img src="https://github.com/user-attachments/assets/8a68a834-3ba6-458a-8f1c-9353a9dd8fee" width="300" height="500" />

---

## 🔧 Backend
- Kotlin (Spring Boot)
- MariaDB (10.11 LTS), Hibernate/JPA
- Gradle 빌드
- OAuth (카카오 로그인)


**주요 기능**
- 사용자/위시리스트/그룹/맛집 데이터 관리 REST API
- OAuth 2.0 카카오 로그인 인증/인가
- JPA 기반 데이터 모델링 및 CRUD

---

## Frontend
- React 18 + TypeScript
- MobX (상태 관리), React Query (서버 상태 관리)
- Tailwind CSS
- pnpm 빌드
- KakaoMap SDK, KakaoTalk SDK

**주요 기능**
- 🏠 그룹 관리: 생성/조회/상세 (최대 15개), 그룹장 권한(👑), 멤버 초대, 카카오톡 초대
- 📍 위치 기반: 현재 위치 기반 맛집 추천, 실시간 지도 마커 표시, 검색 및 선택
- 🎲 랜덤 추천: 셔플 애니메이션, 카드-지도 연동, 부드러운 트랜지션
- 🎨 UI/UX: 반응형 모달, 로딩/에러 처리, 카드형 리스트, 직관적 버튼 인터랙션

---

## 배포 및 운영
- 스크립트를 이용한 한번의 명령어로 로컬 빌드 → Docker Hub 푸시 → 서버에서 Pull & Compose 실행하여 배포.
- Backend/Frontend 컨테이너 분리
- Nginx Reverse Proxy로 라우팅
- MariaDB는 외부 서버 네이티브 설치

---

## 기술적 의의
- 프론트·백 분리 아키텍처 운영 경험
- OAuth 기반 인증/인가 직접 구현
- MobX + React Query 조합 활용
- Docker 기반 자동화 배포
- 위치 기반 서비스 + 랜덤 추천 경험
