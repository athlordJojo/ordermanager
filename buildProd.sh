

cd ./angular-app
ng build --configuration="production"
cd ../
./mvnw package -D skipTests
java -jar target/order-manager-1.0.0.jar