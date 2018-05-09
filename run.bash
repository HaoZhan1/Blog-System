#! /bin/bash
docker-compose up &
mvn clean install
cd target 
java -jar Blog-System-0.0.1-SNAPSHOT.jar
