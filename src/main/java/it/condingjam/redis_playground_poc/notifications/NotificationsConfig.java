package it.condingjam.redis_playground_poc.notifications;

import it.condingjam.redis_playground_poc.ttl.SessionData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
@Profile(NotificationsConfig.PROFILE)
public class NotificationsConfig {

    public static final String PROFILE = "notifications";

    @Bean
    RedisTemplate<byte[], byte[]> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setDefaultSerializer(RedisSerializer.byteArray());

        return redisTemplate;
    }

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            KeyExpiredListener keyExpiredListener) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setTaskExecutor(consumerPool());
        container.setSubscriptionExecutor(new SimpleAsyncTaskExecutor("sub-"));
        //PatternTopic patternTopic = new PatternTopic("__key*__:" + LiveSession.KEY_SPACE + "*");
        PatternTopic patternTopic = new PatternTopic("__key*__:*");
        container.addMessageListener(keyExpiredListener, patternTopic);

        return container;
    }

    private TaskExecutor consumerPool() {
        /*ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(10);
        pool.setMaxPoolSize(15);
        */
        SimpleAsyncTaskExecutor simple = new SimpleAsyncTaskExecutor("consumer-");
        simple.setVirtualThreads(true);
        simple.setConcurrencyLimit(30);
        return simple;
    }
}
