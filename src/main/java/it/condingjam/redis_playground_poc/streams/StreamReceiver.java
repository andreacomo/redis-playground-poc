package it.condingjam.redis_playground_poc.streams;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static it.condingjam.redis_playground_poc.streams.StreamConfig.CONSUMER;
import static it.condingjam.redis_playground_poc.streams.StreamConfig.STREAM;

@Component
@Profile(StreamConfig.PROFILE)
public class StreamReceiver implements StreamListener<String, MapRecord<String, String, String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamReceiver.class);

    private final StringRedisTemplate redisTemplate;

    public StreamReceiver(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    void init() {
        initConsumer();
        consumePending();
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        LOGGER.info("Receiving <{}>. Is virtual? {}", message, Thread.currentThread().isVirtual());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.opsForStream().acknowledge(CONSUMER.getGroup(), message);
        LOGGER.info("Received <{}>", message);
    }

    private void initConsumer() {
        redisTemplate.opsForStream().groups(STREAM).stream()
                .filter(g -> g.groupName().equals(CONSUMER.getGroup()))
                .findFirst()
                .ifPresentOrElse(
                        c -> LOGGER.info("Consumer group already exists"),
                        () -> redisTemplate.opsForStream().createGroup(STREAM, ReadOffset.from(RecordId.of("0-0")), CONSUMER.getGroup()));
    }

    private void consumePending() {
        PendingMessages pendingMessages;
        while (!(pendingMessages = redisTemplate.opsForStream().pending(STREAM, CONSUMER, Range.unbounded(), 20)).isEmpty()) {
            LOGGER.info("Reclaiming pending messages...");
            pendingMessages.stream()
                    .map(PendingMessage::getId)
                    .flatMap(ids -> redisTemplate.<String, String>opsForStream().claim(
                            STREAM,
                            CONSUMER.getGroup(),
                            CONSUMER.getName(),
                            Duration.ofMinutes(1),
                            ids
                    ).stream())
                    .forEach(this::onMessage);
        }
    }
}
