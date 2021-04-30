# ENDPOINT MONITORING

This is a simple REST API Spring Boot microservice for monitors particular urls.

## DB setup
- Make sure you have your MYSQL server running
- If you want to change your Mysql url, username and password go to .\MonitoringEndpoints\src\main\resources\application.properties
- default values 
    - url : jdbc:mysql://localhost:3306/endpointMonitoring
    - username : root
    - password : password
 

## Installation

```shell
$ git clone https://github.com/Zedramcak/MonitoringEndpoints.git
$ cd .\MonitoringEndpoints\
$ .\gradlew bootRun
```
Your app should now be running on [localhost:8080](http://localhost:8080/).

## User database
- User DB will be automatically filled with two users</br>
  
  | user_name        | email           | access_token  |
  | ------------- |:-------------:| -----:|
  | Applifting      | info@applifting.cz | 93f39e2f-80de-4033-99ee-249d92736a25 |
  | Batman     | batman@example.com      |   dcb20f8a-5657-4f1b-9f7f-ce65739b359e |

## CRUD

For every method call accessToken must be provided in the HTTP header

| METHOD  | URL  | DESCRIPTION  |
|---	  |:---:	|---	|
| GET  | /monitoredEndpoint  | Return all Endpoints of the user  |
| POST  | /monitoredEndpoint  | creates new Endpoint for the user  |
| GET  | /monitoredEndpoint/{id}  | Returns Endpoint  |
| PUT  | /monitoredEndpoint/{id}  | Edits Endpoint  |
| DELETE  |  /monitoredEndpoint/{id} | Deletes Endpoint  |
| GET  | /monitoredEndpoint/{id}/monitoringResults  | Returns last 10 Monitoring results for the Endpoint  |