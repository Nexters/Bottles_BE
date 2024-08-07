#!/bin/bash

source /home/${SERVER_USER_NAME}/deploy/env_vars.sh

sed 's/^export //' /home/${SERVER_USER_NAME}/deploy/env_vars.sh > /home/${SERVER_USER_NAME}/docker/.env

sudo docker pull ${DOCKER_USERNAME}/bottles-api:${DOCKER_TAG}
cd ../docker
docker-compose up -d

# 중단된 컨테이너가 존재하는지 확인
EXIT_CONTAINERS=$(docker-compose ps | grep 'Exit 1' | awk '{print $1}')

if [ -n "$EXIT_CONTAINERS" ]; then
  for CONTAINER in $EXIT_CONTAINERS; do
    ../deploy/notify_error.sh "$CONTAINER"
  done
fi

LATEST_TAG=${DOCKER_TAG}
RUNNING_TAG=$(docker-compose ps --format "{{.Image}}" | grep "${DOCKER_USERNAME}/bottles-api" | awk -F: '{print $2}')

if [ "$LATEST_TAG" != "$RUNNING_TAG" ]; then
  ../deploy/notify_error.sh "bottles:$LATEST_TAG 배포를 실패했습니다.\n현재 bottles:$RUNNING_TAG 가 실행중입니다."
fi

sudo docker image prune -f
