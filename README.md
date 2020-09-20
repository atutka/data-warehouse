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
bookapp      java -jar /app.jar            Up      0.0.0.0:8080->8080/tcp  
bookapp_db   docker-entrypoint.sh mongod   Up      0.0.0.0:27017->27017/tcp
````

##### Postman #####

In directory `postman` is exported collection with example requests. 
You can import it to your Postman app.