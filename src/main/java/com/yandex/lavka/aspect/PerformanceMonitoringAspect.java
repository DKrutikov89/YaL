package com.yandex.lavka.aspect;

import com.yandex.lavka.config.PerformanceProperties;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class PerformanceMonitoringAspect {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitoringAspect.class);

    private final MeterRegistry meterRegistry;
    private final PerformanceProperties performanceProperties;

    public PerformanceMonitoringAspect(MeterRegistry meterRegistry, PerformanceProperties performanceProperties) {
        this.meterRegistry = meterRegistry;
        this.performanceProperties = performanceProperties;
    }

    @Around("within(com.yandex.lavka.service..*) || within(com.yandex.lavka.controller..*)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startedAt = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long elapsedNanos = System.nanoTime() - startedAt;
            long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(elapsedNanos);
            String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
            String methodName = joinPoint.getSignature().getName();

            Timer.builder("app.method.execution")
                    .tag("class", className)
                    .tag("method", methodName)
                    .register(meterRegistry)
                    .record(elapsedNanos, TimeUnit.NANOSECONDS);

            if (elapsedMillis >= performanceProperties.getSlowCallThresholdMs()) {
                logger.warn("Slow call detected: {}.{} took {} ms", className, methodName, elapsedMillis);
            } else {
                logger.debug("Measured {}.{} in {} ms", className, methodName, elapsedMillis);
            }
        }
    }
}
