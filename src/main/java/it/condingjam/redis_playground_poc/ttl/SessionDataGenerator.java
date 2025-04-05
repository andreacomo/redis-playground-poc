package it.condingjam.redis_playground_poc.ttl;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Component
@Profile(TtlConfig.PROFILE)
public class SessionDataGenerator implements ApplicationRunner, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionDataGenerator.class);

    private static final long TTL = 20;

    private static final long OFFSET = 5;

    private final SessionDataRepository repository;

    public SessionDataGenerator(SessionDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (int i = 0; i < 2; i++) {
            SessionData sessionData = repository.save(generate());
            LOGGER.info("Saved session id {}", sessionData.getId());
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

    private SessionData generate() {
        SessionData sessionData = new SessionData();
        sessionData.setUserId(RandomStringUtils.secure().nextNumeric(5));
        sessionData.setTtl(TTL);

        return sessionData;
    }
}
