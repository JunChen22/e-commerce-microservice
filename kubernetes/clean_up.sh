#!/bin/bash

# remove the mapping from /etc/hosts
sudo sed -i "\~^$(minikube ip)\\s*springecom\.me\$~d" /etc/hosts

# remove alias from ~/.bashrc
# sed -i '/alias kubectl="minikube kubectl --"/d' ~/.bashrc

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
