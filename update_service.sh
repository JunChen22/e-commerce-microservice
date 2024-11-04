#!/bin/bash

docker-compose down -v
docker-compose build app cms pms
docker-compose up -d eureka gateway cms

