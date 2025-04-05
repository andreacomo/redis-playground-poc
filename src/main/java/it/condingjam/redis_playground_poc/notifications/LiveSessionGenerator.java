package it.condingjam.redis_playground_poc.notifications;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Component
@Profile(NotificationsConfig.PROFILE)
public class LiveSessionGenerator implements ApplicationRunner, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiveSessionGenerator.class);

    private static final long TTL = 20;

    private static final long OFFSET = 5;

    private final RedisTemplate<byte[], byte[]> redisTemplate;

    // https://docs.spring.io/spring-data/redis/reference/redis/hash-mappers.html
    private final ObjectHashMapper mapper = new ObjectHashMapper();

    public LiveSessionGenerator(RedisTemplate<byte[], byte[]> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (int i = 0; i < 2; i++) {
            Map<byte[], byte[]> mapperHash = mapper.toHash(generate());
            String id = LiveSession.KEY_SPACE + ":" + UUID.randomUUID();
            redisTemplate.opsForHash().putAll(id.getBytes(StandardCharsets.UTF_8), mapperHash);
            redisTemplate.expire(id.getBytes(StandardCharsets.UTF_8), Duration.of(TTL, ChronoUnit.SECONDS));

            LOGGER.info("Saved live session id {}", id);
        }
        awaitTtl();
    }

    @Override
    public void destroy() {
        LOGGER.info("Disposing runner");
    }

    private static void awaitTtl() {
        try (var scheduler = Executors.newSingleThreadScheduledExecutor()) {
            LOGGER.info("Waiting for ttl {}s + offset {}s", TTL, OFFSET);
            scheduler.schedule(() -> LOGGER.info("Done"), TTL + OFFSET, TimeUnit.SECONDS);
        }
    }

    private LiveSession generate() {
        LiveSession session = new LiveSession();
        session.setUserId(RandomStringUtils.secure().nextAlphabetic(10));
        session.setLastRequest(LocalDateTime.now());

        return session;
    }
}
