package org.launchcode.TimeTracker.data;

import org.launchcode.TimeTracker.models.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {
}

