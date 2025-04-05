package it.condingjam.redis_playground_poc.queue;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(QueueConfig.PROFILE)
public class QueueConfig {

    public static final String PROFILE = "queue";

    public static final String QUEUE = "echo_queue";
}
