#!/bin/bash
# shellcheck disable=SC2046

docker stop $(docker ps -aq)
echo "Контейнеры остановлены"
docker rm $(docker ps -aq)
echo "Удалены контейнеры"
docker rmi -f $(docker images -q)
echo "Удалены образы"