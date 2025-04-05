package it.condingjam.redis_playground_poc.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Profile(PubSubConfig.PROFILE)
public class PubSubReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(PubSubReceiver.class);

    private final AtomicInteger counter = new AtomicInteger();

    public void receiveMessage(String message) throws InterruptedException {
        LOGGER.info("Receiving <{}>. Is virtual? {}", message, Thread.currentThread().isVirtual());
        TimeUnit.SECONDS.sleep(5);
        counter.incrementAndGet();
        LOGGER.info("Received <{}>", message);
    }

    public int getCount() {
        return counter.get();
    }
}
