#!/bin/bash

minikube kubectl create namespace e-com

# initialization data.q
minikube kubectl -- create configmap postgres-data --from-file=../document/data.sql --namespace=e-com

helm install e-com-dev-env ./environments/dev-env --namespace e-com

minikube kubectl -- config set-context $(minikube kubectl config current-context) --namespace=e-com
