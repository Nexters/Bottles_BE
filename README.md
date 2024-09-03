# 보틀 - 너에게 보내는 편지

<img src="https://github.com/user-attachments/assets/3d6dc61a-3258-429e-878c-66ef3da6b45d" />


[<img height=50px src=https://user-images.githubusercontent.com/42789819/115149387-d42e1980-a09e-11eb-88e3-94ca9b5b604b.png>](https://url.kr/cps1z2)
[<img height=50px src=https://www.fcsok.org/wp-content/uploads/2020/04/get-it-on-google-play-badge.png>]()

> Android: 베타 테스트 중

## Tech Stacks

- **Language**: Kotlin
- **Framework**: Spring Boot
- **Database**: MySQL, JPA
- **Infra**: Docker, Nginx, Github Actions, AWS EC2, AWS S3, FCM
- **Others**: Caffeine Cache

## Infrastructure

<img src="https://github.com/user-attachments/assets/d0ad4c6c-e902-4670-86f0-d5b1b9a3dbce"/>

## CI/CD Flow

### CI

`/.github/workflows/pull-request-workflow.yml`

1. develop 브랜치에 PR이 올라오면 build 실행

### CD

`/.github/workflows/deploy-workflow.yml`

1. develop 브랜치에 merge 되면 실행
2. 도커 이미지 build 및 도커 허브에 이미지 push (api, batch 이미지)
3. docker-compose.yml 및 배포 스크립트 서버에 복사
4. application.yml에 사용되는 환경 변수를 github secret에서 가져와서 EC2 서버에 저장
5. deploy.sh 실행 -> docker image pull 한 뒤 docker-compose.yml을 가지고 docker-compose 실행 (.env 파일에서 환경변수를 읽음)

## Module

### api

통신을 위한 모듈 (controller 레이어)

- 로컬 실행 방법

```shell
cd api/build/libs
java -jar api-0.0.1-SNAPSHOT.jar --spring.profiles.active=local --server.port=8080 
```

### app

도메인 및 도메인 로직을 위한 모듈 (domain, repository, service 레이어)

### batch

배치 작업을 위한 모듈

- 로컬 실행 방법

```shell
cd batch/build/libs
java -jar batch-0.0.1-SNAPSHOT.jar --spring.profiles.active=local --server.port=8081
```

## API 문서

- swagger: http://localhost:8080/swagger-ui

## 설정

vm options: `-Duser.timezone=Asia/Seoul`

## Developers

<table>
<tr>
  <td align="center">
    <a href="https://github.com/injoon2019">
      <img src="https://avatars.githubusercontent.com/u/46641538?v=4" width="150">
    </a>
  </td>
  <td align="center">
    <a href="https://github.com/miseongk">
      <img src="https://avatars.githubusercontent.com/u/39994337?v=4" width="150">
    </a>
  </td>
</tr>
<tr>
  <td align="center">
    <p align="center"><a href="https://github.com/injoon2019">injoon2019</a></p>
  </td>
  <td align="center">
    <p align="center"><a href="https://github.com/miseongk">miseongk</a></p>
  </td>
</tr>
</table>
