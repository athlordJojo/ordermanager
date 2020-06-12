#!/bin/bash

cd ./angular-app
ng build --configuration="production"
cd ../
./mvnw package
