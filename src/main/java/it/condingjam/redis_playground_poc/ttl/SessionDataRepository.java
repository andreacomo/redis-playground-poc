package it.condingjam.redis_playground_poc.ttl;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile(TtlConfig.PROFILE)
public interface SessionDataRepository extends CrudRepository<SessionData, String> {

    List<SessionData> findByUserId(String userId);
}
