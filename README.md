# Redis Advanced playground with Spring Data Redis (Lettuce)

## Profile `pubsub`

Following the [Getting started tutorial by Spring](https://spring.io/guides/gs/messaging-redis)
the outcome is that **Redis PubSub** is used, even if it's not explicitly written nowhere.

## Profile `queue`

Implementing **manual queue logic** as in [Redis Queues documentation](https://redis.io/glossary/redis-queue/)

## Profile `stream`

Following [Spring Redis Streams](https://docs.spring.io/spring-data/redis/reference/redis/redis-streams.html#redis.streams.receive.containers)
and some details from [Redis Stream in action using Java and Spring Data Redis](https://medium.com/@amitptl.in/redis-stream-in-action-using-java-and-spring-data-redis-a73257f9a281)

More complete implementation of queuing system. Requires attention on:

* creating a consumer group first
* retrieve pending messages on start

## Profile `data`

Playground for a Redis repository (ORM-like approach)

See [official documentation](https://redis.io/learn/develop/java/redis-and-spring-course)

## Profile `ttl`

See how data with TTL is managed by Redis repository enabling

```java
@SpringBootApplication
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisPlaygroundPocApplication {

}
```
See: [Time To Live](https://docs.spring.io/spring-data/redis/reference/redis/redis-repositories/expirations.html)
See: [Spring Data Redis — Simple, yet Challenging](https://europace.de/spring-data-redis-simple-yet-challenging/)
See: [How to configure Redis TTL with Spring Data Redis](https://www.baeldung.com/spring-data-redis-ttl#redis-key-expiration-event)

## Profile `notifications`

It's about manual TTL set and expiration notification using the **keyspace notification**

> Requires `config set notify-keyspace-events Ex` to enable notifications
> If you run this profile after `ttl` and keep the container up, this is done for you

See: https://subham-nitd.medium.com/using-redis-keyspace-notifications-for-discard-task-after-the-timeout-f42749e448f3