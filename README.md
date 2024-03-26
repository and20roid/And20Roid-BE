# 안단테

비공개 테스트 테스터 모집을 위한 서비스

## 1. 서비스 개요

[새로운 개인 개발자 계정의 앱 테스트 요구사항 - Play Console 고객센터](https://support.google.com/googleplay/android-developer/answer/14151465?hl=ko)

2023년 11월 13일 이후 구글 플레이스토어 배포 정책 변경으로, 개인 개발자들은 20명의 테스터를 모집하여 2주간 비공개 테스트를 진행해야만 앱을 게시할 수 있게 되었습니다. 그로 인해, 개인 개발자들은 테스터 모집에 어려움을 겪으며 오픈 채팅방을 활용하거나 지인들에게 연락을 돌려야 하는 소요가 발생했습니다. ‘안단테’는 이러한 테스터 모집의 어려움을 해소해 줄 수 있는 서비스로, 다음의 기능을 제공합니다.

- 게시판 기능
- 알림 기능
- 횟수 기록 기능
- 랭킹 기능
- 테스트 요청 기능

안단테 서비스 이동 : [(Google Play Store)](https://play.google.com/store/apps/details?id=com.codekunst.and20roid&pcampaignid=web_share)

## 2. 서비스 구성

- App: Flutter
- Backend: Spring Boot, Spring Data JPA, Spring Security, Firebase Admin
- Database: MySQL
- Deploy: Docker
- Configuration management: Github

## 3. 서비스 디플로이

### Build & Push (로컬에서 수행)

A. 모듈 build

- 디플로이를 원하는 모듈 `clean` & `build`를 통해 `jar` 파일 생성

B. 도커 이미지 build

- 프로젝트 최상단(`Dockerfile`이 위치한)으로 이동하여 도커 이미지 build
    
    ```bash
    docker build --build-arg DEPENDENCY=build/dependency -t {이미지명:태그} --platform linux/amd64 .
    ```
    

C. 도커 이미지 push

- build한 이미지를 도커 허브에 push
    
    ```bash
    docker push {도커 허브 아이디}/{이미지명:태그}
    ```
    

### Deploy (배포 서버에서 수행)

- 도커 이미지 pull
    - 로컬에서 push한 도커 이미지 pull
        
        ```bash
        docker pull {도커 허브 아이디}/{이미지명:태그}
        ```
        
- 도커 컴포즈 실행
    - `docker-compose.yml`이 있는 위치로 이동하여 명령어 수행
        
        ```bash
        docker-compose up -d
        ```
        

## 관련 소스코드 위치

- 안단테 FE ([바로가기](https://github.com/and20roid/And20Roid-FE))
