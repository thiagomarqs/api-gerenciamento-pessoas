package com.github.thiagomarqs.gerenciamentopessoas.domain.repository;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.City;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends CrudRepository<City, Long> {
}
