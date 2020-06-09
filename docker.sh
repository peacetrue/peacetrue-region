#!/bin/bash

echo "1. gradle bootJar"
#./gradlew clear
#./gradlew bootJar

cd peacetrue-region-docker || exit

echo "2. docker-compose down"
docker-compose down

echo "3. docker-compose up --build -d"
docker-compose up --build -d
