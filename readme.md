##Warehouse API
A Microservice to handle warehouse API Operations.
### Getting started
#### Running tests
mvn clean install
### Start the backend
Start as Spring boot web application. and navigate to localhost:8080/swagger-ui.html For Swagger
### Built with
Spring boot 2.5.4,Maven,REST,Google code formatting.
### Test coverage 
Test coverage for used lines is 100%
### Assumptions & Questions
Assume Long datatype is enough for numerics.


Uploading same article file multiple time Action? Increment or replace?Currently it replace.


Assume productName are Unique.

Persisting Quantity ,when articles are inserted more logic is needed.
### Improvement
Load any file in one endpoint.

Use Builder Design pattern.
