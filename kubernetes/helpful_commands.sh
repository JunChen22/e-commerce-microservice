# Set the alias to use Minikube's Kubectl.
# to make it permanent add this line to ~/.bashrc or .bash_profile
alias kubectl="minikube kubectl --"

# reload the bashrc file
source ~/.bashrc

# Configures shell to use Minikube's Docker daemon.
eval $(minikube docker-env)

# Builds Docker images inside Minikube's Docker daemon.
docker-compose build

# Creates a new namespace called 'e-com'.
kubectl create namespace e-com

# Sets the current context to use 'e-com' namespace by default.
kubectl config set-context $(kubectl config current-context) --namespace=e-com


# Deploys resources defined
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl apply -f ingress.yaml

# Lists all Pods in the 'e-com' namespace.
kubectl get pods -n e-com

# Retrieves endpoints for the 'app' Service in the 'e-com' namespace.
kubectl get endpoints -n e-com app


kubectl get service -n e-com app -o=jsonpath='{.spec.selector.matchLabels}'



kubectl logs -n ingress-nginx <ingress-controller-pod-name>





kubectl logs -n e-com <pod-name>




kubectl get service -n ingress-nginx



kubectl get events -n e-com


kubectl get pods

kubectl get ingress

kubectl get pods -n ingress-nginx
kubectl logs <pod-name> -n <namespace-of-ingress-controller>
kubectl logs -n ingress-nginx ingress-nginx-controller-5959f988fd-dz7g9

kubectl delete pods -n ingress-nginx -l app.kubernetes.io/component=controller

kubectl get pods -n ingress-nginx




eval $(minikube docker-env)
docker-compose build

kubectl create namespace demotest
kubectl config set-context $(kubectl config current-context) --namespace=demotest

kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl apply -f ingress.yaml

kubectl delete namespace demotest
kubectl create namespace demotest
kubectl config set-context $(kubectl config current-context) --namespace=demotest

kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl apply -f ingress.yaml




kubectl logs pod/ingress-nginx-controller-5959f988fd-9p9hv -n ingress-nginx




kubectl describe ingress release-name-dev-env






kubectl create namespace e-com

kubectl create configmap postgres-data --from-file=../document/data.sql --namespace e-com

helm install e-com-dev-env ./environments/dev-env --namespace e-com

kubectl config set-context $(kubectl config current-context) --namespace=e-com



helm template .



eval $(minikube docker-env)     // the connect docker to minikube/kuberentes, the build will strictly on minikube/kubernetes and not on docker image list

docker-compose build cms pms oms ums sms app auth-server search


kubectl delete namespace e-com

helm dependency update

kubectl create namespace e-com

helm install e-com-dev-env . --namespace e-com



watch kubectl get all









kubectl get endpoints app







kubectl create namespace first-attempts
kubectl config set-context $(kubectl config current-context) --namespace=first-attempts

kubectl apply -f kubernetes/first-attempts/nginx-deployment.yaml
kubectl apply -f kubernetes/first-attempts/nginx-service.yaml






when applying nginx-deployment
pod
deployment
and replica set is generated


minikube ip
192.128.12.63:30080 will work but not http://localhost:30080



the kubectl get endpoints -n e-com app command did return app    121.174.0.2:80   83s.

helm upgrade ingress-nginx ingress-nginx/ingress-nginx -n ingress-nginx

kubectl get deployment -n e-com app -o=jsonpath='{.spec.selector.matchLabels}'
kubectl get service -n e-com app -o=jsonpath='{.spec.selector.matchLabels}'



  labels:
    app.kubernetes.io/name: app
    helm.sh/chart: app-1.0.0
    app.kubernetes.io/managed-by: Helm


  selector:
    matchLabels:
      app.kubernetes.io/name: {{ include "common.name" . }}


NGINX Ingress controller
  Release:       v1.2.1
  Build:         08848d69e0c83992c89da18e70ea708752f21d7a
  Repository:    https://github.com/kubernetes/ingress-nginx
  nginx version: nginx/1.19.10


// TODO: fix these commands and comments.
