#!/bin/bash

ENV_FILE="/home/abradox/work/BattleBotsConfig/app.env"

docker run --network="host" --env-file "$ENV_FILE" -p 8761:8761 -d admin-service-image
sleep 5
docker run --network="host" --env-file "$ENV_FILE" -p 8083:8083 -d token-service-image
docker run --network="host" --env-file "$ENV_FILE" -p 8085:8085 -d battle-service-image
docker run --network="host" --env-file "$ENV_FILE" -p 8084:8084 -d statistic-service-image
sleep 5
docker run --network="host" --env-file "$ENV_FILE" -p 8081:8081 -d middleware-service-image
docker run --network="host" --env-file "$ENV_FILE" -p 8082:8082 -d crm-service-image
sleep 5
docker run --network="host" --env-file "$ENV_FILE" -p 8080:8080 -d platform-gateway-image
docker run --network="host" --env-file "$ENV_FILE" -p 8090:8090 -d battle-gateway-image