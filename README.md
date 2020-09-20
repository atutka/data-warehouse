# README #

Simple data warehouse

#### Used technologies ####

* kotlin
* spring boot
* spring data jpa
* junit 5 and mockk for testing
* postgresql
* liquibase
* docker

#### How to run? ####

Requirements: docker and docker-compose [Documentation and installation](https://docs.docker.com/engine/install/)

Run script build.sh
````
./build.sh
````
And then run:
````
./run.sh
````
To verify if everything works do:
````
docker-compose ps
````
And you should see something like:
````
   Name                Command             State            Ports          
---------------------------------------------------------------------------
wareehouse      java -jar /app.jar            Up      0.0.0.0:8080->8080/tcp  
warehouse-db   docker-entrypoint.sh mongod   Up      0.0.0.0:27017->27017/tcp
````

##### Postman #####

In directory `Postman` is exported collection with example requests. 
You can import it to your Postman app.
There are also some simple sample requests that can be used.

#### Endpoints #####

1. POST /api/warehouse/import
Imports data from csv file and return id of import in header `location`

2. GET /api/warehouse/import/{id}/status
Returns status for import given id

2. POST /api/warehouse/search
Searches through data and returns results for given request

##### Running instance #####

Application is running on server with address http://54.38.159.208:8080. It has imported data from the file given in description.