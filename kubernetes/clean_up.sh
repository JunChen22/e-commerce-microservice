#!/bin/bash

kubectl delete namespace e-com

# remove charts and Char.lock files
for f in ./components/*; do rm -r $f/charts; done

for f in ./components/*; do rm $f/Chart.lock; done

for f in ./environments/*; do rm -r $f/charts; done

for f in ./environments/*; do rm $f/Chart.lock; done

# unsets the environment variables that were set by the minikube docker-env command. eval $(minikube docker-env)
# eval $(minikube docker-env --unset)

# TODO: might delete profile too