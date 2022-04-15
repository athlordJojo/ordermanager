FROM openjdk:11

RUN mkdir /ordermanager
COPY target/order-manager-1.0.0.jar ./ordermanager/app.jar

CMD java -jar ./ordermanager/app.jar
