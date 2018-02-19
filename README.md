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
* Spring Boot Version - going to use the latest 1.5 version. 
* Group = com.scmc
* Artifact = boot-demo
* Name = boot-demo
* Package Name = com.scmc.bootdemo
* Packaging = JAR
* Java Version = 8
* Dependencies = DevTools, Actuator, Web, JPA, H2, Liquibase

Once selections and entries are complete, click create and the project will be generated and downloaded as a zip file.

Copy the files from the zip file into the project's root directory. 

Let's try running the command to start the application, ".\gradlew bootRun"

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

* Let's start by looking at the only Java class generated
* This is found in the folder source main java
* The package is com.scmc.bootdemo
* This is a simple Java class with one method
* The main method is a Java convention for application entry
* The main method calls the run method on the spring application class
* Note the SpringBootApplication annotation. 
* This annotation simply wraps common conventions for configuring a spring application.
* I'll come back to this class momentarily

**The application properties file can be found here: src/main/resources/application.properties**

* To start, this file doesn't contain anything. 
* This file is one of several ways to set properties. 
* By default, Spring Boot has actually defaulted many properties for you. 
* Again, I'll come back to this momentarily

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
* Let's look at the last artifact generated

**The last file Spring Initializr generated was the gradle build file.**

```groovy
dependencies {
	compile('org.springframework.boot:spring-boot-starter-actuator')
	compile('org.springframework.boot:spring-boot-starter-data-jpa')
	compile('org.liquibase:liquibase-core')
	compile('org.springframework.boot:spring-boot-starter-web')
	runtime('org.springframework.boot:spring-boot-devtools')
	runtime('com.h2database:h2')
	testCompile('org.springframework.boot:spring-boot-starter-test')
}
```

* The final artifact is the gradle build file
* You can find this in the root directory with the name build.gradle
* I'm just showing the dependencies section of the script
* Note that there are four dependencies that have the prefix spring boot starter
* These are specialized libraries provided by Spring Boot to automatically bring in dependencies for you
* For example, the web starter will bring in an embedded Tomcat web server for you. 
* There are several ways you can override these starters. For example, you can 
* Now that you've seen the generated files, let's find out a little more about what Spring Boot is and does. 

**Spring Boot Goal - Provide a radically faster and widely accessible getting-started experience for all Spring development.**

* What we have seen so far is a demonstration of one of the key goals of Spring Boot
* Spring Boot wants you to be able to get started on feature development quickly
* While there were a few hiccups, there's nothing stopping us from adding actual value right now. 
* Let's look at another goal. 

**Spring Boot Goal - Absolutely no code generation and no requirement for XML configuration.**

* As we saw, annotations and the spring boot starters configured everything in the application. 
* There was no XML configuration, a hallmark of earlier Spring
* There was also no code generation needed

**Is Spring Boot just an abstraction over a lot of complexity?** 

* Now you might be thinking, I just showed you an application with one line of code. 
* Is Spring Boot just abstracting us away from a whole bunch of complexity? 
* I can't disagree with this
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
* While there are more attributes available, I'm just simply setting an ID and the class name for each bean.
* When the spring container loads, myBean will be injected as a dependency of yourBean. 
* This structure works well for smaller application, but you end up in XML hell when the application grows. 

**Revisit Spring Boot Application Class**

* Let's revisit the Spring Boot application class that was generated.
* The SpringBootAnnotation is a good example of the path Spring chose over XML
* Spring makes heavy use of Java annotations, which can decorate everything from classes to methods to member variables. 
* SpringBootAnnotation will automatically scan your code for Spring Beans and register them in the container. 
* This annotation will also automatically configure your application context for you. 
* For example, if you have the Tomcat embedded server library on your classpath, it will automatically register and configure a server.

**Spring Boot Goal - Be opinionated out of the box but get out of the way quickly as requirements start to diverge from the defaults.**

* Leveraging annotations in this manner achieves another of Spring Boots' goals. 
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

* That brings us to Spring Boot's final goal
* Boot provides us with a range of features in the form of these starters
* As I noted in the previous goal, Spring Boot takes an opinion on how these features are configured
* You can further customize based on your needs

**Summary**

* Let's summarize what's been talked about so far. 
* You saw how Spring Initializr can be used to create a Spring Boot project
* I showed you all of the options available to you
* I discussed the key goals Spring Boot is trying to achieve
* Get you started quickly 
* No code generation and no XML requirements
* Opinionated out of the box, but get out of the way as requirements necessitate
* Provide support for a range of non-functional features
* Next, let's focus on a common way Spring Boot is currently used.


# 4 - Microservices with Spring Boot

**Architecture Approaches**

* There are two prevailing approaches to architecting systems 
* First is the monolithic application
* Monoliths are the most common approach for development.
* Application is packaged as a single artifact
* Whole application is deployed at once.
* The second approach is Microservices
* Application is broken down to multiple, distributed services
* Services can be deployed independently.
* There is also a gray area 
* Application has several artifacts
* Broken up between front-end and back-end code
* Usually done to support multiple front-ends

**Monolith Vs. Microservices**

* I wasn't planning on getting into a battle over monolith versus microservices
* Monoliths are not dead and they aren't bad. 
* Pattern I'm seeing is to start with a monolith and then decompose
* If you are interested in more, we can discuss after the session. 

**Spring Boot Architecture Support**

* For either monoliths or microservices, Spring Boot is an option to support server-side code
* That said, in this presentation, I'm going to focus on Microservices

**Survey from Redhat on Microservice Adoption**

* The approach of using microservices architecture is continuing to grow at a rapid pace
* Per a Redhat survey, almost 70 percent of respondents are using this approach
* Given the growth of microservices, it should come as no surprise that the Spring Boot team has focused heavily on its support

**Microservices with Spring Boot**

* Let's look at a basic implementation of a Microservices architecture
* I'll start with an Eureka Service Registry

**Eureka Service Registry**

* The Eureka Service registry supports the service discovery pattern in Microservice architecture.
* It is provided by Netflix
* Its also available via Spring Cloud's custom wrapper. 
    * Can create from Spring Initializr
* Eureka is a database of service instances and their locations
* This database gets queried by clients or routers
    * The goal here is to provide discoverability of services
    * It also decouples the client or gateway from direct calls to the microservices

**Eureka Server Demo**

* I'll start by showing you the generated Eureka Server application code
* Then I'll start the server
* Next, I’ll show you the Eureka UI
* Finally, we will update the application we started with for discovery

Demo

* Open Intellij to the Eureka Server application and explain the code
* Note that the code was generated by Spring Initializr
* Open command window to eureka server project and enter .\gradlew bootRun
* Navigate to http://127.0.0.1:8761
* Note there are no instances registered with Eureka
* Let’s update the spring boot app we created earlier to be discovered.
* Open the build.gradle and add the following:

```
dependencyManagement {
  imports {
    mavenBom 'org.springframework.cloud:spring-cloud-dependencies:Edgware.SR2'
  }
}

compile('org.springframework.cloud:spring-cloud-starter-eureka')
testCompile('org.springframework.cloud:spring-cloud-starter-eureka-server’)
```

* Next, open the BootDemoApplication class and add the following annotation on the class and the class for service instances:

```
@EnableEurekaClient

@RestController
class ServiceInstanceRestController {
  @Autowired
  private DiscoveryClient discoveryClient;
  
  @RequestMapping("/service-instances/{applicationName}")
  public List<ServiceInstance> serviceInstancesByApplicationName(
    @PathVariable String applicationName) {
    return this.discoveryClient.getInstances(applicationName);
  }
}
```

* Next, create a file called bootstrap.properties in the resources folder and add the following content:

```
spring.application.name=restaurant-restaurant-service
```

* Finally, run the application from a command prompt using .\gradlew bootRun
* After the application has started, we can go back to the Eureka home page and see the instance now registered.
* Let’s stop the application as we are going to add some more to it. 

**Microservices with Spring Boot**

* We saw how to use Spring Boot, Spring Cloud and Eureka to support the discoverability of Microservices.
* Next, let’s look at how we can support external configuration of our Microservices

**Spring Configuration Server**

* The Spring Configuration Server supports the Externalized Configuration Pattern for Microservice architecture.
* The server is provided as part of the Spring Cloud library
* Commonly, configurations that will change per environment are externalized, such as the database URL
* The server properties are backed by a repository, which is usually Git.  

**Spring Configuration Server Demo**

* I’ll start by showing you the generated Spring Config Server application code
* Next, I’ll show the backing GitHub repository for properties
* Then I’ll start the configuration server
* Finally, I’ll wire up a property to the initial application

Demo

* Open Intellij to the Configuration Server application and explain the code
* Note that the code was generated by Spring Initializr
* Open command window to config server project and enter .\gradlew bootRun
* Navigate to http://localhost:8888/restaurant-service/restaurant-servce
* Note the restaurant-service title property included
* Let’s update the spring boot app we created earlier to be discovered.
* Open the build.gradle and add the following:

```
compile('org.springframework.cloud:spring-cloud-starter-config’)
```

* Next, open the bootstrap.properties file for the service application and add the lines

```
spring.cloud.config.uri=http://localhost:8888 
management.security.enabled=false
```

* Then, add the following to the BootDemoApplication class:

```
@RefreshScope
@RestController
class MessageRestController {

  @Value("${title:Default Title}")
  private String title;

  @RequestMapping("/title")
  String getTitle() {
    return this.title;
  }
}
```

* Finally, run the application from a command prompt using .\gradlew bootRun
* After the application has started, we can navigate here to verify the property: http://localhost:8080/title
* Let’s again stop the application as we are going to add some more to it. 

**Microservices with Spring Boot**

* We’ve now covered discoverability of microservices through Eureka and externalized configuration through Spring Config
* The microservice application we created can now communicate with both. 
* The next step is to create an API gateway for a sample client application to communicate with

**Spring Gateway**

* The Spring Cloud Gateway supports the API gateway pattern for Microservices
* Provided as part of the Spring Cloud library
* An API Gateway is simply a single entry point for all clients, be it web, mobile or IoT
* The gateway allows you to expose different APIs per client. 
    * Mobile may require less data than web, for example
* The gateway can handle your requests in multiple ways
    * Can simply route or proxy the request
    * Can also fan out the request to multiple service instances
    * Can load balance to a discovery service
* Let's look at a gateway implementation now

**Spring Gateway Demo**

* I’ll start by showing you the generated Spring Gateway Server application code
* Then we will start the application and see the route

Demo

* Open Intellij to the Gateway Server application and explain the code
* Note that the code was generated by Spring Initializr
* Show the route and explain mapping to discovery server
* Open command window to gateway server project and enter .\gradlew bootRun
* Navigate to http://localhost:9001/restaurant-service/api/restaurants
* Note that we will have this working once we wire up the service
* Let’s stop the application for now

**Microservices with Spring Boot**

* The final step is to code up our restaurant review API
* This will be implemented as a microservice

**Microservice Demo**

* To finalize the demo for Microservices, I’ll start by creating an entity for our sample microservice
* Next, I’ll create the repository that will query restaurant information from the H2 database
* Then finally, I’ll create the restaurant resource which exposes our api for submitting and viewing ratings on restaurants.


Demo

* Open Intellij to the Sprint Boot demo application
* Create a class called RestaurantEntity in the package com.scmc.bootdemo.domain
* Add the following code to the restaurant entity:

```
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "restaurant")
public class RestaurantEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private Long id;

  @NotNull
  @Column(name = "name", nullable = false)
  private String name;

  @NotNull
  @Column(name = "rating", nullable = false)
  private Integer rating;

  public RestaurantEntity() {
  }

  public RestaurantEntity(String name, Integer rating) {
    this.name = name;
    this.rating = rating;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }
}
```

* Create a class called RestaurantRepository and place it in the package: com.scmc.bootdemo.repository
* Add the following code:

```
import com.scmc.bootdemo.domain.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {

  List<RestaurantEntity> findAllByName(String name);
}
```

* Create a class named RestaurantResource and add it to the package: com.scmc.bootdemo.web.rest
* Add the following code:

```
import com.scmc.bootdemo.domain.RestaurantEntity;
import com.scmc.bootdemo.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.OptionalDouble;

@RestController
@RequestMapping("/api")
public class RestaurantResource {

  private final RestaurantRepository restaurantRepository;
  private final String title;

  public RestaurantResource(RestaurantRepository restaurantRepository,
    @Value("${title}") String title) {
    this.restaurantRepository = restaurantRepository;
    this.title = title;
  }

  @GetMapping("/restaurants")
  public List<RestaurantEntity> getAllRestaurants() {
    return restaurantRepository.findAll();
  }

  @GetMapping("/restaurants/{name}/rating")
  public ResponseEntity<String> getRestaurantsRating(@PathVariable(name = "name") String name) {
    OptionalDouble averageRating =
        restaurantRepository.findAllByName(name)
          .stream()
          .mapToInt(RestaurantEntity::getRating)
          .average();
    if (averageRating.isPresent()) {
      return ResponseEntity.ok(title + " - " + name + " average = " +
          Double.valueOf(averageRating.getAsDouble()).intValue());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("/restaurants")
  public ResponseEntity<RestaurantEntity> createRestaurant(
      @Valid @RequestBody RestaurantEntity restaurantEntity) throws Exception {
    RestaurantEntity result = restaurantRepository.save(restaurantEntity);
    return ResponseEntity
        .created(new URI("/api/restaurants/" + result.getId()))
        .body(result);
  }

}
```

* Next, let’s start the application up
* With all four applications running, we should now be able to test.

Postman

* GET: http://localhost:9001/restaurant-service/api/restaurants
* POST: http://localhost:9001/restaurant-service/api/restaurants  {"name":"McDonalds","rating":5}
* GET: http://localhost:9001/restaurant-service/api/restaurants/McDonalds/rating

**Summary**
