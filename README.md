# CI/CD 및 환경변수
1. /.github/workflows/pull-request-workflow.yml
S3키, NaverSMS키, 토큰키 등을 github secret에서 가져와서 env에 저장.

2. /.github/workflows/deploy-workflow.yml
develop 브랜치 머지될때. 환경변수들 ec2에 쓰고 deploy.sh 실행

3. deploy.sh
ec2에 있는 환경변수들 souce로 가져오고 docker-compose 실행

4. docker-compose.yml을 가지고 docker-compose 실행됨
이때 스프링에 환경변수 넣어주면 applicaion.yml에서 읽음

# 문서
swagger: http://localhost:8080/swagger-ui

# 설정
vm options: `-Duser.timezone=Asia/Seoul`
