EurekaActors
============


#Installation

Have Netflix eureka server running
Download the EurekaActors Code
Make modifications to eureka-client.properties in ServiceHosting and ServiceConsumer for eureka server url

#Running

##ServiceProvider
mvn clean install

##ServiceHosting
run the following command in the ServiceHosting
sbt clean compile dist

bin/start org.akka.essentials.service.ServiceInit

##ServiceConsumer
run the following command in the ServiceConsumer
sbt clean compile dist

bin/start org.akka.essentials.service.ServiceInit

