package com.handgrow.demo.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger {

    private final Environment env;

    public StartupLogger(Environment env) {
        this.env = env;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logStartupInfo() {
        System.out.println("=== STARTUP DEBUG INFO ===");
        System.out.println("Active profiles: " + String.join(",", env.getActiveProfiles()));
        System.out.println("DB_URL exists: " + (env.getProperty("DB_URL") != null));
        System.out.println("JWT_SECRET_KEY exists: " + (env.getProperty("JWT_SECRET_KEY") != null));
        System.out.println("Port: " + env.getProperty("PORT", "not-set"));
        System.out.println("=== END DEBUG INFO ===");
    }
}