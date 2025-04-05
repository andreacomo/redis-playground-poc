package it.condingjam.redis_playground_poc.pubsub;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;


@Configuration
@Profile(PubSubConfig.PROFILE)
public class PubSubConfig {

    public static final String PROFILE = "pubsub";

    public static final String PUB_SUB_TOPIC = "echo_pub_sub";

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setTaskExecutor(consumerPool());
        container.setSubscriptionExecutor(new SimpleAsyncTaskExecutor("sub-"));
        container.addMessageListener(listenerAdapter, new PatternTopic(PUB_SUB_TOPIC));

        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(PubSubReceiver pubSubReceiver) {
        return new MessageListenerAdapter(pubSubReceiver, "receiveMessage");
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
