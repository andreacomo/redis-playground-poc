package it.condingjam.redis_playground_poc.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@Profile(NotificationsConfig.PROFILE)
public class KeyExpiredListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyExpiredListener.class);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String entryKey = new String(message.getBody());
        String channel = new String(message.getChannel());
        LOGGER.info("Received expiration event: {} for key: {}", entryKey, channel);
    }
}
