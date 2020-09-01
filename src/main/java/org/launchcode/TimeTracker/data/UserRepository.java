package org.launchcode.TimeTracker.data;

import org.launchcode.TimeTracker.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

    User findByUsername(String username);

}
