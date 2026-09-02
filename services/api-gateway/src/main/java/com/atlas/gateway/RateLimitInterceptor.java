package com.atlas.gateway;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate redis;
    private final int requestsPerMinute;

    public RateLimitInterceptor(
            StringRedisTemplate redis,
            @Value("${atlas.rate-limit.requests-per-minute:120}") int requestsPerMinute
    ) {
        this.redis = redis;
        this.requestsPerMinute = requestsPerMinute;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equals(request.getMethod()) || request.getRequestURI().endsWith("/health")) {
            return true;
        }

        String identity = clientIdentity(request);
        long minute = Instant.now().getEpochSecond() / 60;
        String key = "rate-limit:" + identity + ":" + minute;

        try {
            Long count = redis.opsForValue().increment(key);
            if (count != null && count == 1) {
                redis.expire(key, Duration.ofSeconds(70));
            }
            response.setHeader("X-RateLimit-Limit", Integer.toString(requestsPerMinute));
            if (count != null && count > requestsPerMinute) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Rate limit exceeded\"}");
                return false;
            }
        } catch (RuntimeException ignored) {
            // Keep the API available if local Redis is down.
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Could not write rate-limit response", exception);
        }
        return true;
    }

    private String clientIdentity(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
