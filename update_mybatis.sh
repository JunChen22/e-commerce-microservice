#!/bin/bash

# Shut down all Docker containers
docker-compose down

# Remove Docker volumes
docker volume rm e-commerce-microservice_db-data
docker volume rm e-commerce-microservice_sms-data
docker volume rm e-commerce-microservice_oms-data
docker volume rm e-commerce-microservice_ums-data
docker volume rm e-commerce-microservice_cms-data
docker volume rm e-commerce-microservice_pms-data
docker volume rm e-commerce-microservice_auth-data
docker volume rm e-commerce-microservice_email-data
docker volume rm e-commerce-microservice_rabbit-data

# Start Docker containers for databases
docker-compose up -d oms-db cms-db ums-db pms-db sms-db auth-db postgres email-db

# Wait for the containers to be healthy
echo "Waiting for containers to be healthy..."
while true; do
    if docker ps --filter "health=healthy" --format "{{.Names}}" | grep -q "oms-db" && \
       docker ps --filter "health=healthy" --format "{{.Names}}" | grep -q "cms-db" && \
       docker ps --filter "health=healthy" --format "{{.Names}}" | grep -q "ums-db" && \
       docker ps --filter "health=healthy" --format "{{.Names}}" | grep -q "pms-db" && \
       docker ps --filter "health=healthy" --format "{{.Names}}" | grep -q "sms-db" && \
       docker ps --filter "health=healthy" --format "{{.Names}}" | grep -q "email-db" && \
       docker ps --filter "health=healthy" --format "{{.Names}}" | grep -q "auth-db" && \
       docker ps --filter "health=healthy" --format "{{.Names}}" | grep -q "postgres"
    then
        echo "Containers are healthy."
        break
    else
        echo "Containers are not healthy yet. Waiting..."
        sleep 5
    fi
done

# Wait for PostgreSQL to be ready to accept connections
echo "Waiting for PostgreSQL to be ready..."
docker-compose run --rm postgres bash -c "while ! pg_isready -h postgres -U postgres -q -t 1; do sleep 5; done"

echo "Containers are not healthy yet. Waiting..."

# Run MyBatis generator for each module
echo "Generating MyBatis files for ECom-admin"
cd ./admin
mvn mybatis-generator:generate -Dmybatis.generator.overwrite=true
cd ..

echo "Generating MyBatis files for ECom-app/App"
cd ./app/App
mvn mybatis-generator:generate -Dmybatis.generator.overwrite=true
cd ../..

echo "Generating MyBatis files for ECom-app/CMS"
cd ./app/CMS
mvn mybatis-generator:generate -Dmybatis.generator.overwrite=true
cd ../../

echo "Generating MyBatis files for ECom-app/OMS"
cd ./app/OMS
mvn mybatis-generator:generate -Dmybatis.generator.overwrite=true
cd ../..

echo "Generating MyBatis files for ECom-app/PMS"
cd ./app/PMS
mvn mybatis-generator:generate -Dmybatis.generator.overwrite=true
cd ../..

echo "Generating MyBatis files for ECom-app/SMS"
cd ./app/SMS
mvn mybatis-generator:generate -Dmybatis.generator.overwrite=true
cd ../..

echo "Generating MyBatis files for ECom-app/UMS"
cd ./app/UMS
mvn mybatis-generator:generate -Dmybatis.generator.overwrite=true
cd ../..

echo "Generating MyBatis files for auth-server"
cd ./auth-server
mvn mybatis-generator:generate -Dmybatis.generator.overwrite=true
cd ../

echo "Generating MyBatis files for Notification"
cd ./notification
mvn mybatis-generator:generate -Dmybatis.generator.overwrite=true
cd ../

docker-compose down
echo "MyBatis generation completed for all modules"


echo "Update sql script for all modules test container"

MODULES=("admin" "app/App" "app/CMS" "app/OMS" "app/PMS" "app/SMS" "app/UMS" "auth-server" "notification" "search")
SOURCE_PATH="./document/data.sql"

# Loop through each module name and copy data.sql for test container
for MODULE_NAME in "${MODULES[@]}"; do
    DEST_PATH="$MODULE_NAME/src/test/resources/data_test_copy.sql"
    cp "$SOURCE_PATH" "$DEST_PATH"
    echo "Copied $SOURCE_PATH to $DEST_PATH"
done
