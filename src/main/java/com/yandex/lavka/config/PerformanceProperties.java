package com.yandex.lavka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.performance")
public class PerformanceProperties {

    private long slowCallThresholdMs = 200;

    public long getSlowCallThresholdMs() {
        return slowCallThresholdMs;
    }

    public void setSlowCallThresholdMs(long slowCallThresholdMs) {
        this.slowCallThresholdMs = slowCallThresholdMs;
    }
}
