package it.condingjam.redis_playground_poc.data;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
@Profile(DataConfig.PROFILE)
public class DataConfig {

    public static final String PROFILE = "data";
}
