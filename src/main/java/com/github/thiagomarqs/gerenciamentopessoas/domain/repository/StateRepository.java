package com.github.thiagomarqs.gerenciamentopessoas.domain.repository;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.State;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends CrudRepository<State, Long> {
}
