#!/bin/bash

# Step 1: Create the e-com namespace
echo "Creating namespace 'e-com'..."
minikube kubectl -- create namespace e-com

# Step 2: Apply the namespace YAML configuration
echo "Applying the e-com namespace configuration..."
minikube kubectl -- apply --filename environments/e-com-namespace.yaml --namespace e-com

# Step 3: Create the PostgreSQL config map with initialization data
echo "Creating PostgreSQL configmap with initialization data..."
minikube kubectl -- create configmap postgres-data --from-file=../document/data.sql --namespace=e-com

# Step 4: Install Helm chart for e-com development environment
echo "Installing Helm chart for the e-com development environment..."
helm install e-com-dev-env ./environments/dev-env --namespace e-com

# Step 5: Set the current Kubernetes context to the e-com namespace
echo "Setting Kubernetes context to 'e-com' namespace..."
minikube kubectl -- config set-context --current --namespace=e-com

# Step 6: Provide instructions for the user to manually start minikube tunnel and update /etc/hosts
echo ""
echo "### MANUAL STEPS REQUIRED ###"
echo ""
echo "Step 1: Start 'minikube tunnel' in another terminal:"
echo "   $ minikube tunnel"
echo ""
echo "Step 2: Once 'minikube tunnel' is running, set the Istio ingress gateway IP to /etc/hosts."
echo "Run the following commands:"
echo ""
echo "   INGRESS_IP=\$(minikube kubectl -- get service istio-ingressgateway -n istio-system -o jsonpath='{.status.loadBalancer.ingress[0].ip}')"
echo "   echo \"Ingress IP: \$INGRESS_IP\" for debugging/confirmation"
echo ""
echo "   sudo bash -c \"echo '\$INGRESS_IP springecom.me' >> /etc/hosts\""
echo ""
echo "Verify that you can access the service via http://springecom.me"
echo ""