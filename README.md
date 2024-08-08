# Module

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

# CI/CD Flow

1. /.github/workflows/pull-request-workflow.yml
   S3키, NaverSMS키, 토큰키 등을 github secret에서 가져와서 env에 저장

2. /.github/workflows/deploy-workflow.yml
   develop 브랜치 머지될 때 환경변수들 ec2에 쓰고 deploy.sh 실행

3. deploy.sh
   ec2에 있는 환경변수들 source로 가져오고 docker-compose 실행

4. docker-compose.yml을 가지고 docker-compose 실행됨
   이때 스프링에 환경변수 넣어주면 applicaion.yml에서 읽음

# 문서

swagger: http://localhost:8080/swagger-ui

# 설정

vm options: `-Duser.timezone=Asia/Seoul`
