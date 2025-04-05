package it.condingjam.redis_playground_poc.queue;

import it.condingjam.redis_playground_poc.commons.RunningStatus;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static it.condingjam.redis_playground_poc.queue.QueueConfig.QUEUE;

@Component
@Profile(QueueConfig.PROFILE)
public class QueueSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueSender.class);

    private final StringRedisTemplate redisTemplate;

    private final RunningStatus runningStatus;

    public QueueSender(StringRedisTemplate redisTemplate, RunningStatus runningStatus) {
        this.redisTemplate = redisTemplate;
        this.runningStatus = runningStatus;
    }

    @PostConstruct
    void run() throws Exception {
        for (int i = 0; i < 100; i++) {
            var counter = i + 1;
            LOGGER.info("Sending message {}", counter);
            redisTemplate.opsForList().leftPush(QUEUE, "Message in queue n. " + counter);
        }
        while (redisTemplate.opsForList().size(QUEUE) > 0) {
            TimeUnit.SECONDS.sleep(1);
        }
        runningStatus.stop();
    }
}
