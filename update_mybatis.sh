#!/bin/bash

# Shut down all Docker containers
docker-compose down

# Remove Docker volumes
docker volume rm e-commerce-microservice_db-data
docker volume rm e-commerce-microservice_SMS-data
docker volume rm e-commerce-microservice_OMS-data
docker volume rm e-commerce-microservice_UMS-data
docker volume rm e-commerce-microservice_CMS-data
docker volume rm e-commerce-microservice_PMS-data

# Start Docker containers for databases
docker-compose up -d OMS-db CMS-db UMS-db PMS-db SMS-db postgres

# Wait for the containers to be healthy
echo "Waiting for containers to be healthy..."
while true; do
    if docker ps --filter "health=healthy" --format "{{.Names}}" | grep -q "OMS-db" && \
       docker ps --filter "health=healthy" --format "{{.Names}}" | grep -q "CMS-db" && \
       docker ps --filter "health=healthy" --format "{{.Names}}" | grep -q "UMS-db" && \
       docker ps --filter "health=healthy" --format "{{.Names}}" | grep -q "PMS-db" && \
       docker ps --filter "health=healthy" --format "{{.Names}}" | grep -q "SMS-db" && \
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
cd ./ECom-admin
mvn mybatis-generator:generate -Dmybatis.generator.overwrite=true
cd ..

echo "Generating MyBatis files for ECom-app/App"
cd ./ECom-app/App
mvn mybatis-generator:generate -Dmybatis.generator.overwrite=true
cd ../..

echo "Generating MyBatis files for ECom-app/CMS"
cd ./ECom-app/CMS
mvn mybatis-generator:generate -Dmybatis.generator.overwrite=true
cd ../../

echo "Generating MyBatis files for ECom-app/OMS"
cd ./ECom-app/OMS
mvn mybatis-generator:generate -Dmybatis.generator.overwrite=true
cd ../..

echo "Generating MyBatis files for ECom-app/PMS"
cd ./ECom-app/PMS
mvn mybatis-generator:generate -Dmybatis.generator.overwrite=true
cd ../..

echo "Generating MyBatis files for ECom-app/SMS"
cd ./ECom-app/SMS
mvn mybatis-generator:generate -Dmybatis.generator.overwrite=true
cd ../..

echo "Generating MyBatis files for ECom-app/UMS"
cd ./ECom-app/UMS
mvn mybatis-generator:generate -Dmybatis.generator.overwrite=true
cd ../..

echo "MyBatis generation completed for all modules"
