# Spring Cloud Microservices with Eureka, Hystrix and Zuul API Gateway 

## Components
- `eureka-service` - Eureka Service Registry
- `account-server` -  A Sample REST MicroService.
- `aggregator-service` -  Another REST Service Scaffold, which can discover account-service from Service Registry
- `zuul-service` - Gateway/Edge Service which is registered with Eureka and routes the requests to Client and Server using Eureka Service.
