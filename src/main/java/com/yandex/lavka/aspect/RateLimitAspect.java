package com.yandex.lavka.aspect;

import com.yandex.lavka.exception.RateLimitExceededException;
import com.yandex.lavka.ratelimit.RateLimit;
import com.yandex.lavka.ratelimit.RateLimitKeyType;
import com.yandex.lavka.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitAspect {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitAspect.class);

    private final RateLimiterService rateLimiterService;

    public RateLimitAspect(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Around("@annotation(com.yandex.lavka.ratelimit.RateLimit)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        RateLimit rateLimit = methodSignature.getMethod().getAnnotation(RateLimit.class);
        HttpServletRequest request = currentRequest();
        String key = buildKey(request, rateLimit.keyType());

        if (!rateLimiterService.tryConsume(key, rateLimit.requestsPerSecond(), rateLimit.burstCapacity())) {
            logger.warn("Rate limit exceeded for key={}", key);
            throw new RateLimitExceededException(key, rateLimit.requestsPerSecond());
        }

        return joinPoint.proceed();
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attributes.getRequest();
    }

    private String buildKey(HttpServletRequest request, RateLimitKeyType keyType) {
        String ip = resolveClientIp(request);
        String path = request.getMethod() + ":" + request.getRequestURI();

        return switch (keyType) {
            case IP -> ip;
            case PATH -> path;
            case IP_AND_PATH -> ip + "|" + path;
        };
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr();
    }
}
