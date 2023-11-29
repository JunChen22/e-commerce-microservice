#!/bin/bash

# TODO: might add download minikube too and make kubectl alias as part of setup

# start a different profile
minikube start -p spring

# switching profile
minikube profile spring

# enable/start ingress controller
minikube addons enable ingress

cd ../

mvn clean install

# connect docker to minikube/kubernetes, the build will strictly on minikube/kubernetes and not on docker image list
eval $(minikube docker-env)

docker-compose build cms pms oms ums sms app auth-server search notification

docker pull postgres:9.6.10
docker pull mongo:5.0.0
docker pull redis:7.0.14
docker pull rabbitmq:3.8.11-management
docker pull elasticsearch:7.12.0
docker pull logstash:7.12.0
docker pull kibana:7.12.0
docker pull openzipkin/zipkin:2.23.2

# just to show current addon list
minikube addons list

minikube addons enable ingress   # gateway

minikube addons enable metrics-server

# just to show current addon list
minikube addons list

# Map springecom.me to the IP address we can use to reach the Minikube instance by adding a line to the /etc/hosts file:
echo Will mapping springeecom.me to $(minikube ip) in /etc/hosts
sudo bash -c "echo $(minikube ip) springecom.me | tee -a /etc/hosts"

echo 'alias kubectl="minikube kubectl --"' >> ~/.bashrc
