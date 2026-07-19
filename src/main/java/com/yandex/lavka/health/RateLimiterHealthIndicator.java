package com.yandex.lavka.health;

import com.yandex.lavka.service.RateLimiterService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class RateLimiterHealthIndicator implements HealthIndicator {

    private final RateLimiterService rateLimiterService;

    public RateLimiterHealthIndicator(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public Health health() {
        return Health.up()
                .withDetail("trackedBuckets", rateLimiterService.getTrackedBucketCount())
                .build();
    }
}
