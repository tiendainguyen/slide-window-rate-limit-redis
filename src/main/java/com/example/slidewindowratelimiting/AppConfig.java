package com.example.slidewindowratelimiting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @Bean
  public RateLimitInterceptor rateLimitInterceptor() {
    return new RateLimitInterceptor(redisTemplate,"rate_limit" ,5, 60 * 1000); // Limit to 10 requests per minute
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(rateLimitInterceptor());
  }
}
