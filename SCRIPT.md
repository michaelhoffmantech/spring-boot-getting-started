# Presentation Demo Script

# Laptop Setup

* Make sure plugged-in for power
* Test dual-screen
* Open a browser tab to http://start.spring.io
* Open a browser tab to http://localhost:8080/health
* Open a browser tab to http://localhost:8761/
* Open a browser tab to http://localhost:8888/restaurant-service/restaurant-service
* Open a browser tab to http://localhost:9001/restaurant-service/api/restaurants
* Open four command prompts and change directory to c:\boot-demo
* IntelliJ running with dashboard
* IntelliJ running with Eureka app
* IntelliJ running with Config app
* IntelliJ running with Gateway app
* Open Postman

# Slide 3 - Spring Initializr Demo

## 1 - Start.Spring.IO

* Build tool - Gradle
* Language - Java
* Spring Boot Version - 1.5.10
* Group = com.scmc
* Artifact = restaurant-service
* Name = restaurant-service
* Package Name = com.scmc.restaurantservice
* Packaging = JAR
* Java Version = 8
* Dependencies = DevTools, Actuator, Web, JPA, H2, Liquibase

## 2 - Download and Run

* Download boot app to c:\boot-demo
* Unzip
* Execute

## 3 - Fixing Liquibase Error

* Add project to IntelliJ by import
* Change the JRE to 1.8
* Create a file under src/main/resources/db/changelog/db.changelog-master.yaml
* Add the following content:

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

* In command prompt, run gradle bootRun again
* Explain start-up log
* Open a browser and look at health endpoint

# Slide 30 - Eureka Server

* Open IntelliJ with Eureka code
  * Show Application class and EnableEurekaServer annotation
  * Show properties
  * Show build script
* Open command prompt to Eureka server start server
* Open browser to http://localhost:8761/ and explain Eureka page

# Slide 33 - Config Server

* Open IntelliJ with Config code
  * Show Application class and Config annotation
  * Show properties
  * Show build script
* Open command propmt to Config server start server
* Open browser to https://github.com/michaelhoffmantech/spring-boot-config-repo and show repo
* Open browser to http://localhost:8888/restaurant-service/restaurant-service and show property

# Slide 36 - API Gateway

* Open IntelliJ with Gateway code
  * Show Application class and route
  * Show properties
  * Show build script
* Open command prompt to Gateway server start server
* Open browser to http://localhost:9001/restaurant-service/api/restaurants and tell why nothing shows

# Slide 38 - Microservice Demo

* Open IntelliJ with Restaurant Service

## Build Script Updates

* Need Microservice to be discoverable and configurable.
* Add dependencies

```
apply plugin: 'idea'
apply plugin: 'io.spring.dependency-management'

dependencyManagement {
	imports {
		mavenBom 'org.springframework.cloud:spring-cloud-dependencies:Edgware.SR2'
	}
}

compile('org.springframework.cloud:spring-cloud-starter-eureka')
compile('org.springframework.cloud:spring-cloud-starter-config')
```

* Apply Gradle changes in IntelliJ

## Application Properties Updates

* For spring Cloud, need a bootstrap property file
  * Loads before the application context
* In src/main/resources, create a file called bootstrap.properties
* Add contents:

```
spring.application.name=restaurant-service
spring.cloud.config.uri=http://localhost:8888
```

* Now can update application properties

```
management.endpoints.web.expose=*
management.security.enabled=false
spring.jackson.serialization.indent_output=true
```

* Properties are for observability, discuss after running
* Enables management endpoints and pretty print management data output

## Boot Application Class

* Add an annotation so that this is discovered by Eureka

```
@EnableEurekaClient
```

## Entity Creation

* Create a package named domain
* Create a new Java class named RestaurantEntity
* Add the following code:

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

* JPA entity with fields
* Matches the schema inserted into H2 by changelog

## Repository

* Create a package named repository
* Create a class named RestaurantRepository

```
import com.scmc.restaurantservice.domain.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {

  List<RestaurantEntity> findAllByName(String name);
}
```

* JPA repository to allow for queries against restaurants
* Added additional method to find all restaurants by name.

## Resource

* Create a package named web.rest
* Create a class named RestaurantResource
* Add the following code:

```
import com.scmc.restaurantservice.domain.RestaurantEntity;
import com.scmc.restaurantservice.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Value;
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

* Created a rest controller
* Injected confi property for title
* Exposed API get all restaurants
* Exposed API to get restaurant ratings
* Exposed api to post restaurant ratings

## Run the microservice

* Open command prompt and run restaurant service
* Note log for service registration
* Open browser to Eureka http://localhost:8761/
  * Note emergency message
* Open browser to http://localhost:8080/health
  * Note discovery client, config and DB
  * Hystrix is a circuit breaker, for example failures
* Open browser to API Gateway http://localhost:9001/restaurant-service/api/restaurants
  * Note this doesn't work yet
  * It should, currently working on why
* Open command prompt back to API gateway, stop and restart
* Open browser to API Gateway http://localhost:9001/restaurant-service/api/restaurants
  * Now see an empty array
  
## Test the service

* Now let's open Postman
* Hit each API endpoint

## Observability

* If there is time, show some of the other actuator endpoints




















