package com.example.slidewindowratelimiting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Set;

//@Component
public class RateLimitInterceptor implements HandlerInterceptor {

  private RedisTemplate<String, String> redisTemplate;
  private String key;
  private int limit;
  private long window;

  public RateLimitInterceptor(RedisTemplate<String, String> redisTemplate, String key, int limit, long window) {
    this.redisTemplate = redisTemplate;
    this.key = key;
    this.limit = limit;
    this.window = window;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String identifier = request.getHeader("X-Forwarded-For"); // Use the client IP address as the identifier
    if (identifier == null) {
      identifier = request.getRemoteAddr();
    }

    boolean allowed = allowRequest(identifier);
    if (!allowed) {
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.getWriter().write("Rate limit exceeded");
      return false;
    }

    return true;
  }

  private boolean allowRequest(String identifier) {
    long now = System.currentTimeMillis();
    String nowStr = String.valueOf(now);
    double score = Double.parseDouble(nowStr);

    // Add the identifier to the sorted set with the current timestamp as score
    redisTemplate.opsForZSet().add(key, identifier, score);

    // Remove any elements from the sorted set that have a score less than the current time window
    double minScore = score - window;
    redisTemplate.opsForZSet().removeRangeByScore(key, 0, minScore);

    // Count the number of elements in the sorted set
    Set<String> set = redisTemplate.opsForZSet().range(key, 0, -1);
    int count = set.size();

    // If the count is greater than or equal to the limit, deny the request
    if (count >= limit) {
      return false;
    }

    return true;
  }
}

