package it.condingjam.redis_playground_poc.data;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile(DataConfig.PROFILE)
public interface RoleRepository extends CrudRepository<Role, String> {

    List<Role> findByName(String name);
}