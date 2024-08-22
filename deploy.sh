#!/bin/bash

source /home/${SERVER_USER_NAME}/deploy/env_vars.sh

sed 's/^export //' /home/${SERVER_USER_NAME}/deploy/env_vars.sh > /home/${SERVER_USER_NAME}/docker/.env

sudo docker pull ${DOCKER_USERNAME}/bottles-api:${DOCKER_TAG}
cd ../docker

RUNNING_BLUE=$(sudo docker ps --filter "name=springboot_blue" --filter "status=running" -q)
RUNNING_GREEN=$(sudo docker ps --filter "name=springboot_green" --filter "status=running" -q)

if [ -n "$RUNNING_BLUE" ]; then
  AFTER_RUNNING_CONTAINER="springboot_green"
  BEFORE_RUNNING_CONTAINER="springboot_blue"

  echo "Blue 인스턴스가 실행 중입니다. Green 인스턴스를 배포합니다."
  docker-compose up -d springboot_green db
else
  AFTER_RUNNING_CONTAINER="springboot_blue"
  BEFORE_RUNNING_CONTAINER="springboot_green"

  echo "Green 인스턴스가 실행 중입니다. Blue 인스턴스를 배포합니다."
  docker-compose up -d springboot_blue db
fi

sleep 20

RUNNING_NGINX=$(sudo docker ps --filter "name=nginx" --filter "status=running" -q)
if [ -z "$RUNNING_NGINX" ]; then
  docker-compose up -d nginx
fi

if docker-compose ps $AFTER_RUNNING_CONTAINER | grep -q "Up"; then
  sed '' /etc/nginx/conf.d/$AFTER_RUNNING_CONTAINER > /etc/nginx/conf.d/${SERVER_NGINX_CONF}
  sudo docker exec nginx nginx -s reload
  echo "Nginx 설정을 변경했습니다."

  sleep 10

  echo "$BEFORE_RUNNING_CONTAINER 를 중단합니다."
  docker-compose stop $BEFORE_RUNNING_CONTAINER
else
  echo "$AFTER_RUNNING_CONTAINER 가 실행 중이 아닙니다. Nginx 설정을 변경하지 않습니다."
fi

# 중단된 컨테이너가 존재하는지 확인
EXIT_CONTAINERS=$(docker-compose ps | grep 'Exit 1' | awk '{print $1}')

if [ -n "$EXIT_CONTAINERS" ]; then
  for CONTAINER in $EXIT_CONTAINERS; do
    ../deploy/notify_error.sh "$CONTAINER"
  done
fi

sudo docker image prune -f
