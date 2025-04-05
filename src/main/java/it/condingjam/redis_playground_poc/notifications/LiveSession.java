package it.condingjam.redis_playground_poc.notifications;

import java.time.LocalDateTime;

public class LiveSession {

    public static final String KEY_SPACE = "live";

    private String userId;

    private LocalDateTime lastRequest;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(LocalDateTime lastRequest) {
        this.lastRequest = lastRequest;
    }
}
