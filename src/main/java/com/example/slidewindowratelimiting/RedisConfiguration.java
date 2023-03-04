package com.example.slidewindowratelimiting;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration{

  // Better if you can use a properties file and inject these values
  private String redisHost = "localhost";
  private int redisPort = 6379;

  @Bean
  JedisConnectionFactory jedisConnectionFactory() {
    JedisConnectionFactory factory = new JedisConnectionFactory();
    factory.setHostName(redisHost);
    factory.setPort(redisPort);
    factory.setUsePool(true);
    return factory;
  }

  @Bean
  RedisTemplate< String, Object > redisTemplate() {
    final RedisTemplate< String, Object > template =  new RedisTemplate< String, Object >();
    template.setConnectionFactory( jedisConnectionFactory() );
    template.setKeySerializer( new StringRedisSerializer() );
    template.setHashValueSerializer( new GenericToStringSerializer< Object >( Object.class ) );
    template.setValueSerializer( new GenericToStringSerializer< Object >( Object.class ) );
    return template;
  }
}
