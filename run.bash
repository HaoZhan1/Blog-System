#! /bin/bash
mvn clean install
cd target 
java -jar enterprise-web-development-0.0.1-SNAPSHOT.jar 
