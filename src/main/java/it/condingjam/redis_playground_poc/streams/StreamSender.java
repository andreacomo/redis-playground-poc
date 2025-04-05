package it.condingjam.redis_playground_poc.streams;

import it.condingjam.redis_playground_poc.commons.RunningStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static it.condingjam.redis_playground_poc.streams.StreamConfig.CONSUMER;
import static it.condingjam.redis_playground_poc.streams.StreamConfig.STREAM;

@Component
@Profile(StreamConfig.PROFILE)
public class StreamSender implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamSender.class);

    private final StringRedisTemplate redisTemplate;

    public StreamSender(StringRedisTemplate redisTemplate, RunningStatus runningStatus, StreamReceiver streamReceiver) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (int i = 0; i < 100; i++) {
            var counter = i + 1;
            LOGGER.info("Sending message {}", counter);
            redisTemplate.opsForStream().add(
                    StreamRecords.string(Map.of("msg", "Message in queue n. " + counter))
                            .withStreamKey(STREAM),
                    RedisStreamCommands.XAddOptions.maxlen(150)
            );
            TimeUnit.MILLISECONDS.sleep(500);
        }
        while (redisTemplate.opsForStream().groups(STREAM).stream()
                .anyMatch(info -> info.groupName().equals(CONSUMER.getGroup()) && info.pendingCount() > 0)) {
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
