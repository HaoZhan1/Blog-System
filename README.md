# Blog-System
The blog system which is constructed by SpringBoot.

#### Install the Maven:

* wget http://www.trieuvan.com/apache/maven/maven-3/3.5.3/binaries/apache-maven-3.5.3-bin.tar.gz
* tar xzvf apache-maven-3.5.3-bin.tar.gz
* sudo mv apache-maven-3.5.3 /usr/local/apache-maven
* export PATH=$PATH:/usr/local/apache-maven/bin

#### Run the project:

* Go to the enterprise-web-development folder.
* RUN `cd src/main/resources/` and modify the "spring.datasource.username" and "spring.datasource.password" in the "application.properties" to get access to your MySQL Database.
* Run `bash run.bash` to start the project (Your need to install the maven & java firstly).
* Type into "http://localhost:8080/main/index" to get access to the Main Page.

