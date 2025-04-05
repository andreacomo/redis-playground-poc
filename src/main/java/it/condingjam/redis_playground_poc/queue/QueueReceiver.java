package it.condingjam.redis_playground_poc.queue;

import it.condingjam.redis_playground_poc.commons.RunningStatus;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static it.condingjam.redis_playground_poc.queue.QueueConfig.QUEUE;

@Component
@Profile(QueueConfig.PROFILE)
public class QueueReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueReceiver.class);

    private final AtomicInteger counter = new AtomicInteger();

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private final StringRedisTemplate redisTemplate;

    private final RunningStatus runningStatus;

    public QueueReceiver(StringRedisTemplate redisTemplate, RunningStatus runningStatus) {
        this.redisTemplate = redisTemplate;
        this.runningStatus = runningStatus;
    }

    @PostConstruct
    void initPoll() {
        executor.execute(() -> {
            while (runningStatus.isRunning()) {
                String value = redisTemplate.opsForList().rightPop(QUEUE, Duration.ofSeconds(5));
                if (value != null) {
                    receiveMessage(value);
                }
            }
            close();
        });
    }

    @PreDestroy
    void close() {
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
            executor.shutdown();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void receiveMessage(String message) {
        LOGGER.info("Receiving <{}>. Is virtual? {}", message, Thread.currentThread().isVirtual());
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        counter.incrementAndGet();
        LOGGER.info("Received <{}>", message);
    }

    public int getCount() {
        return counter.get();
    }
}
