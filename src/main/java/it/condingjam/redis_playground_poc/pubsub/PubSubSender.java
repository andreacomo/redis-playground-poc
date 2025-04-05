package it.condingjam.redis_playground_poc.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static it.condingjam.redis_playground_poc.pubsub.PubSubConfig.PUB_SUB_TOPIC;

@Component
@Profile(PubSubConfig.PROFILE)
public class PubSubSender implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(PubSubSender.class);

    private final StringRedisTemplate redisTemplate;

    private final PubSubReceiver pubSubReceiver;

    public PubSubSender(StringRedisTemplate redisTemplate, PubSubReceiver pubSubReceiver) {
        this.redisTemplate = redisTemplate;
        this.pubSubReceiver = pubSubReceiver;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        int j = 1;
        while (pubSubReceiver.getCount() < 10000) {
            for (int i = 0; i < 100; i++) {
                int messageCount = j*i;
                LOGGER.info("Sending a message {}", messageCount);
                redisTemplate.convertAndSend(PUB_SUB_TOPIC, "The echo " + messageCount);
            }
            TimeUnit.SECONDS.sleep(10);
            j++;
        }
    }
}
