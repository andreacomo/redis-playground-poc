package it.condingjam.redis_playground_poc.ttl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.stereotype.Component;

@Component
@Profile(TtlConfig.PROFILE)
public class KeyExpiredListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyExpiredListener.class);

    @EventListener
    public void handleRedisKeyExpiredEvent(RedisKeyExpiredEvent<SessionData> event) {
        SessionData expiredSession = (SessionData) event.getValue();
        if (expiredSession != null) {
            LOGGER.info("Session with key={} has expired", expiredSession.getId());
        } else {
            LOGGER.info("Might be the expiration of the phantom record... but this should not happen because Spring Data Redis deletes all on expire");
        }
    }
}
