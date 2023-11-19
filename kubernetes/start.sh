#!/bin/bash

alias kubectl="minikube kubectl --"

kubectl create namespace e-com

# initialization data.q
kubectl create configmap postgres-data --from-file=../document/data.sql --namespace e-com

helm install e-com-dev-env ./environments/dev-env --namespace e-com

kubectl config set-context $(kubectl config current-context) --namespace=e-com
