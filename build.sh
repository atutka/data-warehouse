#!/bin/bash

./gradlew clean build
docker build -t warehouse .
docker-compose build