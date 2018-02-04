# spring-boot-getting-started

Repository for my presentation on getting started with Spring Boot in a Microservices environment

# 1 - Introduction

Hi, I'm Michael Hoffman. All the details from my presentation can be found here in this repository. 

Some highlights of my background:

* I author for Pluralsight.com. Pluralsight provides online technical training courses on almost every topic you can imagine. Content is produced by some of the top authors in the industry and comes at a reasonable monthly price. 
* I work for NVISIA based out of Chicago and Milwaukee. We are a consultancy providing product development, legacy modernization, IT strategy and agile strategy services. Our primary focus is on Java-based solutions. 
* I've been working with Spring Boot since it was first released in 2014 and watched it grow and mature over the past three years from an unknown framework to a leader in providing rapid development of server-based applications, especially in the realm of REST-based microservices. 

# 2 - Spring Initializr Demo

## Creating the Project

I'll be starting by demonstrating the creation of a Spring Boot application. I'll be generating the code assets using the website http://start.spring.io. 

Here are some details on selections and entries from this form:

* Build tool - selecting Gradle to leverage code for our build scripts over XML configuration. 
* Language - selecting Java, but also options available for JVM languages Kotlin and Groovy
* Spring Boot Version - going to use the new version 2.0 release candidate. 
* Group = com.scmc
* Artifact = boot-demo
* Name = boot-demo
* Package Name = com.scmc.bootdemo
* Packaging = JAR
* Java Version = 9
* Dependencies = DevTools, Actuator, Web, JPA, H2, Liquibase

Once selections and entries are complete, click create and the project will be generated and downloaded as a zip file.

Copy the files from the zip file into the project's root directory. 

Let's try running the command to start the application, ".\gradlew bootRun"

You might get a failure due to Java 9. 

## Updating the Gradle Wrapper

The RC of Spring Boot was not setting the correct version of Gradle to use with Java 9, so I needed to add the following in the build.gradle file:

```Java
task wrapper(type: Wrapper) {
	gradleVersion = '4.5'
}
```

I deleted the .gradle folder and gradlew wrapper files (make sure you don't delete the build.gradle file), then I ran the command "gradle wrapper" from the root directory. 

Now try running the start command again, ".\gradlew bootRun". 

You should get another failure.

## Fixing the Liquibase Error

I selected Liquibase for managing the database schema, but Spring Initializr didn't start me off with a changelog file. 

By default, the Spring Boot configuration is looking for a changelog in the following location:

db/changelog/db.changelog-master.yaml

Create this file now in the project's src/main/resources folder and add the content below:

```
databaseChangeLog:
  - changeSet:
      id: 1
      author: hoffman
      changes:
        - createTable:
            tableName: restaurant
            columns:
              - column:
                  name: id
                  type: bigint
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: name
                  type: varchar(255)
                  constraints:
                    nullable: false
              - column:
                  name: rating
                  type: int
                  constraints:
                    nullable: false
```

Try running the command ".\gradlew bootRun" and again you will get a failure. 

## Fixing the missing JAXB dependency from Hibernate

As part of changes in Java 9, JAXB is no longer correctly brought in by the Spring Initializr project. Hibernate will cause a ClassNotFoundException due to the missing dependency. 

To fix this, open the build.gradle file and add the dependency below:

compile('javax.xml.bind:jaxb-api:2.3.0')

Now run the command ".\gradlew bootRun" and it should run successfully with the message below at the conclusion of the log:

```
2018-02-04 11:12:33.235  INFO 12952 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2018-02-04 11:12:33.245  INFO 12952 --- [  restartedMain] com.example.demo.DemoApplication         : Started DemoApplication in 11.638 seconds (JVM running for 12.499)
```

