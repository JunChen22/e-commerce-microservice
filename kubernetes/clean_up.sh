#!/bin/bash

# unsets the environment variables that were set by the minikube docker-env command. eval $(minikube docker-env)
eval $(minikube docker-env --unset)

kubectl delete namespace e-com

# delete profile
minikube delete -p spring

# remove charts and Char.lock files
for f in ./components/*; do rm -r $f/charts; done

for f in ./components/*; do rm $f/Chart.lock; done

for f in ./environments/*; do rm -r $f/charts; done

for f in ./environments/*; do rm $f/Chart.lock; done

sudo sed -i '/springecom\.me/d' /etc/hosts
