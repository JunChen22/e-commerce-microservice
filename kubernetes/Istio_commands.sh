The Istio ingress gateway is reached using a different IP address than the IP address used to access the Kubernetes Ingress controller, so we also need to update the IP address mapped to the hostname, minikube.me, which we use when running tests. This is handled in the Setting up access to Istio services section.

The Istio ingress gateway is configured as a load-balanced Kubernetes service; that is, its type is LoadBalancer. To be able to access the gateway, we need to run a load balancer in front of the Kubernetes cluster.


need minikube tunnel
minikube tunnel. This command assigns an external IP address to each load-balanced Kubernetes service, including the Istio ingress gateway.


kubectl -n istio-system get service istio-ingressgateway

kubectl get svc -n istio-system