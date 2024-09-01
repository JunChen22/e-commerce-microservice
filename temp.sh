#!/bin/bash

# Shut down all Docker containers
docker-compose down

mvn clean install -Peureka

docker-compose build

docker-compose up -d eureka gateway redis app ums cms auth-server

