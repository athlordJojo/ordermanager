#!/bin/zsh
./mvnw package -DskipTests
docker build -t "joanimage/ordermanager:1.0.6" .
kubectl apply -f k8s/order-manager-deployment.yaml
