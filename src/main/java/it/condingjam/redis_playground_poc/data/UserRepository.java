package it.condingjam.redis_playground_poc.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

    User findFirstByEmail(String email);
}
