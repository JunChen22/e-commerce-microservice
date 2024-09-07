#!/bin/bash

# Get the IP address of the istio-ingressgateway
INGRESS_IP=$(minikube kubectl -- get service istio-ingressgateway -n istio-system -o jsonpath='{.status.loadBalancer.ingress[0].ip}')

# Output the IP address to the console for debugging/confirmation
echo "Istio Ingress IP: $INGRESS_IP"

# Remove any lines from /etc/hosts that contain the IP and domain
sudo sed -i "\~^$INGRESS_IP[[:space:]]*springecom\.me\$~d" /etc/hosts

# remove alias from ~/.bashrc
# sed -i '/alias kubectl="minikube kubectl --"/d' ~/.bashrc

# unsets the environment variables that were set by the minikube docker-env command. eval $(minikube docker-env)
eval $(minikube docker-env --unset)

minikube kubectl delete namespace e-com

# delete profile
minikube delete -p spring

# remove charts and Char.lock files
for f in ./components/*; do rm -r $f/charts; done

for f in ./components/*; do rm $f/Chart.lock; done

for f in ./environments/*; do rm -r $f/charts; done

for f in ./environments/*; do rm $f/Chart.lock; done
