#!/bin/bash
# shellcheck disable=SC2046

docker stop $(docker ps -aq)

docker rm $(docker ps -aq)

ENV_FILE="/home/abradox/work/BattleBotsConfig/app.env"

docker run --network="host" --env-file "$ENV_FILE" -d admin-service-image
sleep 5
docker run --network="host" --env-file "$ENV_FILE" -d token-service-image
docker run --network="host" --env-file "$ENV_FILE" -d battle-service-image
docker run --network="host" --env-file "$ENV_FILE" -d statistic-service-image
sleep 5
docker run --network="host" --env-file "$ENV_FILE" -d middleware-service-image
docker run --network="host" --env-file "$ENV_FILE" -d crm-service-image
sleep 5
docker run --network="host" --env-file "$ENV_FILE" -d platform-gateway-image
docker run --network="host" --env-file "$ENV_FILE" -d battle-gateway-image