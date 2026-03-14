package com.revpay.userservice.client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class NotificationClientFallback implements NotificationClient {
    private static final Logger log = LoggerFactory.getLogger(NotificationClientFallback.class);
    @Override
    public void createNotification(Map<String, Object> payload) {
        log.warn("[FALLBACK] notification-service unavailable. Dropped: {}", payload);
    }
}
