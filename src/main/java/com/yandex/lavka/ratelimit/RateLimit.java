package com.yandex.lavka.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    int requestsPerSecond() default -1;

    int burstCapacity() default -1;

    RateLimitKeyType keyType() default RateLimitKeyType.IP_AND_PATH;
}
