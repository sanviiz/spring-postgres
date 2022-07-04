# Spring Boot With PostgreSQL

What's in this project?
 - Create endpoint API by using Spring framework.
 - CRUD by using JPA Repository with pagination.
 - Unit test by using JUnit, AssertJ and Mockito.
 
## Quick Overview

First, change directory in your terminal to this project.

### Run Server

Check dependycies(`pom.xml`) working properly before run server:
```
mvn validate
```
Run microservices server:
```
mvn spring-boot:run
```

### Run Unit Test

Run services test:
```
mvn test -Dtest=TutorialServiceTest
```
Run controller test:
```
mvn test -Dtest=TutorialControllerTest
```
Or, run every test in this project:
```
mvn test
```
