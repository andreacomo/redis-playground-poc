package it.condingjam.redis_playground_poc.streams;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@Profile(StreamConfig.PROFILE)
public class StreamConfig {

    public static final String PROFILE = "streams";

    public static final String STREAM = "echo_stream";

    public static final Consumer CONSUMER = Consumer.from("playground-group", "runner-1");

    @Bean(initMethod = "start", destroyMethod = "stop")
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer(
            RedisConnectionFactory connectionFactory,
            StreamReceiver streamReceiver) throws InterruptedException {
        var container = StreamMessageListenerContainer.create(
                connectionFactory,
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(Duration.of(1, ChronoUnit.SECONDS))
                        .executor(consumerPool())
                        .build()
        );

        Subscription subscription = container.receive(
                CONSUMER,
                StreamOffset.create(STREAM, ReadOffset.lastConsumed()),
                streamReceiver);
        subscription.await(Duration.of(5, ChronoUnit.SECONDS));
        return container;
    }

    private TaskExecutor consumerPool() {
        /*ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(10);
        pool.setMaxPoolSize(15);
        */
        SimpleAsyncTaskExecutor simple = new SimpleAsyncTaskExecutor("consumer-");
        simple.setVirtualThreads(true);
        simple.setConcurrencyLimit(10);
        return simple;
    }
}
