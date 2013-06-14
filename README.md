EurekaActors
============

When building services based on the remote actor model, there is a need for dynamic discovery of new remote actor nodes or even loadbalance among the remote actor nodes.

This is a working sample of using Netflix Eureka to load balance, dynamic discovery and handling fault tolerance for remote actor nodes.

If you are looking for master-worker, grid or workload distribution model, clustered actors might be a better option.


# Installation

1. Have Netflix eureka server running (refer - https://github.com/Netflix/eureka)
2. Download the EurekaActors Code
3. Make modifications to eureka-client.properties in ServiceHosting and ServiceConsumer for eureka server url

# Running

## ServiceProvider
mvn clean install

## ServiceHosting

Run the following command in the ServiceHosting

sbt clean compile dist

Go to target/ServiceHosting-dist

bin/start org.akka.essentials.service.ServiceInit

## ServiceConsumer
Run the following command in the ServiceConsumer

sbt clean compile dist

Go to target/ServiceConsumer-dist

bin/start org.akka.essentials.service.ServiceInit

