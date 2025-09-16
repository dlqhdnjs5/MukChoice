
<img width="512" height="512" alt="mukchoice_icon" src="https://github.com/user-attachments/assets/8edf837b-93cc-4469-821b-0678d7e6c137" />


# 먹초이스 - 위치 기반 맛집 추천 서비스
https://mukchoice.kr

2025.06.01~ 2025.09.14

프로젝트 인원: 이보원

> **“내 위치와 그룹 친구들과 함께, 랜덤으로 즐기는 맛집 선택의 재미”**
> 
> 점심시간, 데이트, 친구와의 자리에서 뭘먹을지 고민될땐 먹초이스에게 선택을 맡겨보세요!
 <img width="500" height="300" alt="image" src="https://github.com/user-attachments/assets/098dd4fb-74d5-4e9a-9fca-289b6c3671ad" />

(모바일에서도 즐길 수 있습니다.)

---

## 프로젝트 개요
**Mukchoice**는 위치 기반으로 맛집을 추천하고, 그룹 관리와 랜덤 추천 기능을 통해 즐겁게 의사결정을 지원하는 서비스입니다. <br>
평소 어느메뉴를 고를지, 어느 식당을 고를지에 대한 고민이 많은 저와 주위사람에게 재미난 기능을 제공하기 위해 시작하였습니다. <br>
프론트엔드와 백엔드를 완전히 분리하고, Docker 기반 배포 파이프라인을 구축하여 운영 환경에서도 안정성을 확보했습니다.  <br>
평소 스터디를 하며 신기하고 재밌는 기능을 채워나갈 예정입니다. <br>

---



## 🚀 Backend
**Languages**
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white)


**Frameworks & ORM**
![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=flat&logo=springboot&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=flat&logo=hibernate&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-59666C?style=flat&logoColor=white)

**Database**
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=flat&logo=mariadb&logoColor=white)

**Build Tool**
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Docker Compose](https://img.shields.io/badge/Docker_Compose-2496ED?style=flat&logo=docker&logoColor=white)


**Auth**
![OAuth](https://img.shields.io/badge/OAuth-3E8EDE?style=flat&logo=oauth&logoColor=white)
![Kakao](https://img.shields.io/badge/Kakao%20Login-FFCD00?style=flat&logo=kakao&logoColor=000000)

**주요 기능**
- 사용자/위시리스트/그룹/맛집 데이터 관리 REST API
- OAuth 2.0 카카오 로그인 인증/인가
- JPA 기반 데이터 모델링 및 CRUD



---

## 🎨 Frontend
**Core**
![React](https://img.shields.io/badge/React-61DAFB?style=flat&logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=flat&logo=typescript&logoColor=white)

**State Management**
![MobX](https://img.shields.io/badge/MobX-FF9955?style=flat&logo=mobx&logoColor=white)
![React Query](https://img.shields.io/badge/React%20Query-FF4154?style=flat&logo=reactquery&logoColor=white)

**Styling**
![TailwindCSS](https://img.shields.io/badge/TailwindCSS-06B6D4?style=flat&logo=tailwindcss&logoColor=white)

**Build Tool**
![pnpm](https://img.shields.io/badge/pnpm-F69220?style=flat&logo=pnpm&logoColor=white)

**External APIs**
![Kakao Map](https://img.shields.io/badge/KakaoMap-FFCD00?style=flat&logo=kakao&logoColor=000000)
![KakaoTalk](https://img.shields.io/badge/KakaoTalk-FFCD00?style=flat&logo=kakaotalk&logoColor=000000)

**주요 기능**
- 🏠 그룹 관리: 생성/조회/상세 (최대 15개), 그룹장 권한(👑), 멤버 초대, 카카오톡 초대
- 📍 위치 기반: 현재 위치 기반 맛집 추천, 실시간 지도 마커 표시, 검색 및 선택
- 🎲 랜덤 추천: 셔플 애니메이션, 카드-지도 연동, 부드러운 트랜지션
- 🎨 UI/UX: 반응형 모달, 로딩/에러 처리, 카드형 리스트, 직관적 버튼 인터랙션




---

## 기능 소개
### 로그인
빠르고 편리하게 카카오 소셜 로그인을 통해 계정을 관리합니다.

<img width="500" height="300" alt="image" src="https://github.com/user-attachments/assets/e3a11b57-6e6e-4bb1-8cd7-e45b0c43826c" />


### 홈
2km 내 맛집을 검색할 수 있으며 카테고리별 1km 내 맛집을 탐방해보며 랜덤 초이스를 해보실 수 있습니다. 

**검색결과, 카테고리별 랜덤 초이스**

<img src="https://github.com/user-attachments/assets/7ecdbb55-ab8f-4c98-b128-609373973484" width="300" height="500" />
<img src="https://github.com/user-attachments/assets/8a68a834-3ba6-458a-8f1c-9353a9dd8fee" width="300" height="500" />

### 위시리스트
원하는 맛집을 위시리스트에 저장해두고, 랜덤 초이스로 즐겁게 선택하세요. 지역별 랜덤 초이스 기능도 지원합니다.

**전체 및 지역별 위시리스트 관리, 랜덤 초이스**


<img src="https://github.com/user-attachments/assets/24fb3836-476c-4e47-96f2-ffad9d5f37fd" width="300" height="500" />


### 그룹
원하는 맛집을 그룹으로 관리해 보세요.

**그룹내 랜덤 초이스**

<img width="400" height="350" alt="image" src="https://github.com/user-attachments/assets/e0929fee-740e-4ba0-aeb1-dc705aa374a0" />
<img width="400" height="350" alt="image" src="https://github.com/user-attachments/assets/fc374116-0616-4669-8e43-8197db420e1f" />



<br/>
<br/>


**그룹 초대**

카카오 공유하기를 통해 그룹에 초대하여 맛집을 공유할 수 있습니다.

<img width="400" height="350" alt="image" src="https://github.com/user-attachments/assets/b672d318-b9f5-4d5f-984f-da73876e4559" />
<img width="278" height="383" alt="image" src="https://github.com/user-attachments/assets/05e79faf-57a5-461f-9acb-96ce04a3e655" />


### 마이페이지

마이페이지에서 소셜로그인 로그아웃이 가능합니다.

**로그아웃**

<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/51b47ebb-a304-4d98-8142-54ddc93bf603" />


---

## 아키텍처

### 서비스 시스템 인프라 아키텍처
<img width="1970" height="1013" alt="image" src="https://github.com/user-attachments/assets/333eb8d5-905b-4db3-add0-ad843b3ebcd1" />


### 런타임 네트워크/포트 아키텍처

```mermaid
sequenceDiagram
    autonumber
    participant C as Client (Browser/Mobile)
    participant NG as Nginx (443, TLS Termination)
    participant FE as Frontend Server (:3000)
    participant BE as Backend (Spring Boot, :8080)

    C->>NG: HTTPS GET/POST/...
    NG->>FE: proxy http://127.0.0.1:3000/ (frontend)
    FE-->>NG: HTML/CSS/JS
    NG-->>C: 200 OK (TLS)

    C->>NG: HTTPS XHR /api/...
    NG->>BE: proxy http://127.0.0.1:8080/ (backend)
    BE-->>NG: JSON
    NG-->>C: 200 OK (TLS)
```


---

## 배포 및 운영
- 스크립트를 이용한 한번의 명령어로 로컬 빌드 → Docker Hub 푸시 → 서버에서 Pull & Compose 실행하여 배포.
- Backend/Frontend 컨테이너 분리
- Nginx Reverse Proxy로 라우팅
- MariaDB는 외부 서버 네이티브 설치

---

## 기술적 의의
- 서버 환경 구축 ~ 어플리케이션 배포 자동화까지의 경험.
- Kotlin + JPA/Hibernate 을 이용한 백엔드 구현.
- 프론트·백 분리 아키텍처 운영 경험
- OAuth 기반 인증/인가 직접 구현
- MobX + React Query 조합 활용
- Docker 기반 자동화 배포
- 위치 기반 서비스 + 랜덤 추천 경험
