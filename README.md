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

```
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

You can verify the application is up by hitting the URL: 

http://localhost:8080/actuator/health

# 3 - Spring Boot Deep Dive

We now have a working Spring Boot application. This is just one approach for creating a Spring Boot project. We could have also used a CLI tool or a generator called JHipster to create the project. As a next step, we are going to deep dive into what Spring Initializr generated. 

**Spring Boot Application Class**

* Let's start with the only Java class that was generated in the project. 
* You can find this in the folder src/main/java/com/scmc/bootdemo with the name: BootDemoApplication.java. 
* Below are the contents:

```java
@SpringBootApplication
public class BootDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootDemoApplication.class, args);
	}
}
```

* Let’s start by looking at the only Java class generated
* This is found in the folder source main java
* The package is com.scmc.bootdemo
* This is a simple Java class with one method
* The main method is a Java convention for application entry
* The main method calls the run method on the spring application class
* Note the SpringBootApplication annotation. 
* This annotation simply wraps common conventions for configuring a spring application.
* I’ll come back to this class momentarily

**The application properties file can be found here: src/main/resources/application.properties**

* To start, this file doesn’t contain anything. 
* This file is one of several ways to set properties. 
* By default, Spring Boot has actually defaulted many properties for you. 
* Again, I’ll come back to this momentarily

**Next, Spring Initializr generated a test class for you.** 

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class BootDemoApplicationTests {
	@Test
	public void contextLoads() {
	}
}
```

* A test class was generated for the project.
* This test class is found in the source test java folder. 
* The package name is com dot scmc dot bootdemo
* The test name is BootDemoApplicationTests.java
* This is just a simple test for loading the application context.
* Again, we see several annotations on the class. 
* SpringRunner means that Junit will use the spring runner class for test execution
* SpringBootTest provides Spring Boot support for configuration
* Let’s look at the last artifact generated

**The last file Spring Initializr generated was the gradle build file.**

```groovy
dependencies {
	compile('org.springframework.boot:spring-boot-starter-actuator')
	compile('org.springframework.boot:spring-boot-starter-data-jpa')
	compile('org.liquibase:liquibase-core')
	compile('org.springframework.boot:spring-boot-starter-web')
	compile('javax.xml.bind:jaxb-api:2.3.0')
	runtime('org.springframework.boot:spring-boot-devtools')
	runtime('com.h2database:h2')
	testCompile('org.springframework.boot:spring-boot-starter-test')
}
```

* The final artifact is the gradle build file
* You can find this in the root directory with the name build.gradle
* I’m just showing the dependencies section of the script
* Note that there are four dependencies that have the prefix spring boot starter
* These are specialized libraries provided by Spring Boot to automatically bring in dependencies for you
* For example, the web starter will bring in an embedded Tomcat web server for you. 
* There are several ways you can override these starters. For example, you can 
* Now that you’ve seen the generated files, let’s find out a little more about what Spring Boot is and does. 

**Spring Boot Goal - Provide a radically faster and widely accessible getting-started experience for all Spring development.**

* What we have seen so far is a demonstration of one of the key goals of Spring Boot
* Spring Boot wants you to be able to get started on feature development quickly
* While there were a few hiccups, there’s nothing stopping us from adding actual value right now. 
* Let’s look at another goal. 

**Spring Boot Goal - Absolutely no code generation and no requirement for XML configuration.**

* As we saw, annotations and the spring boot starters configured everything in the application. 
* There was no XML configuration, a hallmark of earlier Spring
* There was also no code generation needed

**Is Spring Boot just an abstraction over a lot of complexity?** 

* Now you might be thinking, I just showed you an application with one line of code. 
* Is Spring Boot just abstracting us away from a whole bunch of complexity? 
* I can’t disagree with this
* Given that Boot is an abstraction, its important to understand some of the foundations of Spring before jumping into Boot

**Spring Framework Features**

* There are four key features of the Spring Framework
* First, the Spring Framework supports and wraps most of the Java Enterprise Edition APIs
* One major omission was Enterprise JavaBeans
* The goal of wrapping these specifications was to simplify Java enterprise development
* A second feature is open source. 
* You can freely look at all of the code right on GitHub. 
* Foundational to Spring is its support of dependency injection. 
* Instead of directly declaring a dependency in my class, I simply inject it during construction. 
* One of the best things about Spring is its testability. 
* Testing is a core concept to the framework and was a major improvement over the J2EE offering. 
* Dependency injection makes it much easier for me to mock out objects and simplify my tests.

**Inversion of Control Container**

* To elaborate more on dependency injection, Spring provides an Inversion of Control container
* You register Spring Beans in the container. A bean can be any Java class. 
* During construction of a Spring Bean, dependencies get injected.  
* These dependencies are immutable. 
* By default, a dependency is a singleton. Each time a dependency is called, you are actually calling a cached version.
* Finally, beans are tied to an application context.  

**XML Configuration**

* As I noted earlier, a goal of Spring Boot was no XML configuration.
* Early on, Spring was heavy on XML.

```xml
<beans>
	<bean id="myBean" class="com.ex.MyBeanEx" />
	<bean id="yourBean" class="com.ex.YourBeanEx">
		<property name="myBean" ref="myBean" />
	</bean>
</beans>
``` 

* This is an example of a context using XML configuration
* Note here that two beans are defined. 
* While there are more attributes available, I’m just simply setting an ID and the class name for each bean.
* When the spring container loads, myBean will be injected as a dependency of yourBean. 
* This structure works well for smaller application, but you end up in XML hell when the application grows. 

**Revisit Spring Boot Application Class**

* Let’s revisit the Spring Boot application class that was generated.
* The SpringBootAnnotation is a good example of the path Spring chose over XML
* Spring makes heavy use of Java annotations, which can decorate everything from classes to methods to member variables. 
* SpringBootAnnotation will automatically scan your code for Spring Beans and register them in the container. 
* This annotation will also automatically configure your application context for you. 
* For example, if you have the Tomcat embedded server library on your classpath, it will automatically register and configure a server.

**Spring Boot Goal - Be opinionated out of the box but get out of the way quickly as requirements start to diverge from the defaults.**

* Leveraging annotations in this manner achieves another of Spring Boots’ goals. 
* Spring Boot is opinionated. 
* By using auto-configuration, Spring Boot automatically starts your application with certain dependencies. 
* Specifically, this is done through Spring Boot Starter dependencies. 

**Gradle Build File**

* We saw those starter dependencies here in the gradle build script dependencies section.
* The web starter brought in an embedded tomcat and set defaults for the server, such as port 8080
* Actuator brought in metrics
* JPA brought in Hibernate dependencies. 
* Test brought in JUnit 

**Spring Framework Offerings**

* Spring offers a wide range of libraries, each with different implementation options. 
* For example, Spring Security supports different approaches for security, including Oauth and JWT
* Spring Boot in turn provides starters for these offerings, along with many others, such as libraries for logging and metrics.

**Spring Boot Goal - Provide a range of non-functional features that are common to large classes of projects (such as embedded servers, security, metrics, health checks, and externalized configuration)**

* That brings us to Spring Boot’s final goal
* Boot provides us with a range of features in the form of these starters
* As I noted in the previous goal, Spring Boot takes an opinion on how these features are configured
* You can further customize based on your needs


