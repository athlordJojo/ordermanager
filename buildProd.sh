#!/bin/bash

cd ./angular-app || exit
ng test --watch=false
ng build --configuration="production"
cd ../
./mvnw package
