#!/bin/bash

# TODO: might add download minikube too and make kubectl alias as part of setup

# start a different profile
minikube start -p spring

# switching profile
minikube profile spring

cd ../

# maven build
mvn clean install

# connect docker to minikube/kubernetes, the build will strictly on minikube/kubernetes and not on docker image list
eval $(minikube docker-env)

docker-compose build cms pms oms ums sms app auth-server search notification

docker pull postgres:16-bullseye
docker pull mongo:5.0.0
docker pull redis:7.0.14
docker pull rabbitmq:3.8.11-management
docker pull elasticsearch:7.12.0
docker pull logstash:7.12.0
docker pull kibana:7.12.0
docker pull openzipkin/zipkin:2.23.2

cd ./kubernetes

# installing Istio
# curl -L https://istio.io/downloadIstio | sh -
cd istio-* # I'm using istio-1.23.0
export PATH=$PWD/bin:$PATH  # temporarily add the path to the istioctl executable to your system’s PATH variable to run the tool from any terminal session.
# installing to minikube, answer y to any prompts during the installation
echo "y" | istioctl install --set profile=default
istioctl experimental precheck

cd ../

./update_helm_dep.sh

# rendered_out_template.txt is the output of the rendered template, easier to debug
helm template ./environments/dev-env/ > ./environments/dev-env/rendered_out_template.txt
helm template ./environments/prod-env/ > ./environments/prod-env/rendered_out_template.txt

# enable/start ingress controller, gateway, not needed after service mesh, Istio
# minikube addons enable ingress

# minikube addons enable metrics-server

# just to show current addon list
minikube addons list

# Map springecom.me to the IP address we can use to reach the Minikube instance by adding a line to the /etc/hosts file:
# this have been changed when switching from Kubernetes/minikube ingress to Istio
#echo Will mapping springeecom.me to $(minikube ip) in /etc/hosts
#sudo bash -c "echo $(minikube ip) springecom.me | tee -a /etc/hosts"

# Istio Gateway external IP will be stored in /etc/hosts file so that we can access the application using springecom.me
# in the browser or minikube tunnel.

# Fetch the IP address of the ingress gateway's external IP and store it in a variable
# INGRESS_IP=$(minikube kubectl -- get service istio-ingressgateway -n istio-system -o jsonpath='{.status.loadBalancer.ingress[0].ip}')

# Output the IP address to the console (for debugging/confirmation)
# echo "Ingress IP: $INGRESS_IP"

# Append the ingress IP and domain to /etc/hosts using sudo
# Ensure to properly pass the variable to the sudo bash command
# sudo bash -c "echo '$INGRESS_IP springecom.me' >> /etc/hosts"


# echo 'alias kubectl="minikube kubectl --"' >> ~/.bashrc

# reload the bashrc file
# source ~/.bashrc