package com.scmc.bootdemo.web.rest;

import com.scmc.bootdemo.domain.RestaurantEntity;
import com.scmc.bootdemo.repository.RestaurantRepository;
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
