package com.yandex.lavka.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yandex.lavka.config.RateLimitProperties;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {

    private final RateLimitProperties properties;
    private final Cache<String, TokenBucketState> buckets;
    private final Counter allowedCounter;
    private final Counter rejectedCounter;

    public RateLimiterService(RateLimitProperties properties, MeterRegistry meterRegistry) {
        this.properties = properties;
        this.buckets = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(30))
                .maximumSize(10_000)
                .recordStats()
                .build();
        this.allowedCounter = Counter.builder("app.rate.limit.requests")
                .tag("result", "allowed")
                .register(meterRegistry);
        this.rejectedCounter = Counter.builder("app.rate.limit.requests")
                .tag("result", "rejected")
                .register(meterRegistry);
    }

    public boolean tryConsume(String key, int requestsPerSecond, int burstCapacity) {
        int configuredRate = requestsPerSecond > 0 ? requestsPerSecond : properties.getDefaultLimit();
        int configuredBurst = burstCapacity > 0 ? burstCapacity : properties.getBurstCapacity();

        TokenBucketState bucketState = buckets.get(key, ignored ->
                new TokenBucketState(configuredRate, configuredBurst, properties.getDurationSeconds()));

        boolean consumed = bucketState.tryConsume();
        if (consumed) {
            allowedCounter.increment();
        } else {
            rejectedCounter.increment();
        }
        return consumed;
    }

    public long getTrackedBucketCount() {
        return buckets.estimatedSize();
    }

    public void clearBuckets() {
        buckets.invalidateAll();
    }

    private static final class TokenBucketState {
        private final int refillTokens;
        private final int capacity;
        private final long refillPeriodNanos;
        private double availableTokens;
        private long lastRefillNanos;

        private TokenBucketState(int refillTokens, int capacity, long durationSeconds) {
            this.refillTokens = refillTokens;
            this.capacity = capacity;
            this.refillPeriodNanos = TimeUnit.SECONDS.toNanos(Math.max(durationSeconds, 1));
            this.availableTokens = capacity;
            this.lastRefillNanos = System.nanoTime();
        }

        private synchronized boolean tryConsume() {
            refill();
            if (availableTokens < 1) {
                return false;
            }
            availableTokens -= 1;
            return true;
        }

        private void refill() {
            long now = System.nanoTime();
            long elapsedNanos = now - lastRefillNanos;
            if (elapsedNanos <= 0) {
                return;
            }

            double tokensToAdd = ((double) elapsedNanos / refillPeriodNanos) * refillTokens;
            if (tokensToAdd <= 0) {
                return;
            }

            availableTokens = Math.min(capacity, availableTokens + tokensToAdd);
            lastRefillNanos = now;
        }
    }
}
