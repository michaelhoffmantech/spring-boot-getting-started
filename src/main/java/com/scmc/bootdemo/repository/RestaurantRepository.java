package com.scmc.bootdemo.repository;

import com.scmc.bootdemo.domain.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {

  List<RestaurantEntity> findAllByName(String name);
}
