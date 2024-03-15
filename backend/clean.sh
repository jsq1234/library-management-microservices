#!/bin/zsh

docker-compose down

echo "Removing gateway-image"
docker image rm gateway-image

echo "Removing user-service-image"
docker image rm user-service-image

echo "Removing book-service-image"
docker image rm book-service-image
