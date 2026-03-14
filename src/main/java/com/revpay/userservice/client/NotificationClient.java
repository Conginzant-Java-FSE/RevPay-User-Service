package com.revpay.userservice.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "notification-service", fallback = NotificationClientFallback.class)
public interface NotificationClient {
    @PostMapping("/internal/notifications/create")
    void createNotification(@RequestBody Map<String, Object> payload);
}
