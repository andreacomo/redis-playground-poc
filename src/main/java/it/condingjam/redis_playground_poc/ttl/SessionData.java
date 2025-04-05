package it.condingjam.redis_playground_poc.ttl;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.concurrent.TimeUnit;

import static it.condingjam.redis_playground_poc.ttl.SessionData.KEY_SPACE;

@RedisHash(value = KEY_SPACE)
public class SessionData {

    public static final String KEY_SPACE = "SessionData";

    @Id
    private String id;

    @Indexed
    private String userId;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long ttl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }
}
