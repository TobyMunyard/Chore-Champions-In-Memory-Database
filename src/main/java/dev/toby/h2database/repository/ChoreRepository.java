package dev.toby.h2database.repository;

import org.springframework.data.repository.CrudRepository;
import dev.toby.h2database.model.Chore;

public interface ChoreRepository extends CrudRepository<Chore, Integer>{
    
}
