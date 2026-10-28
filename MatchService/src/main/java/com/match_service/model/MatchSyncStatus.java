package com.match_service.model;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Component;

@Component
public class MatchSyncStatus {

    private final AtomicBoolean initialSyncCompleted = new AtomicBoolean(false);

    public boolean isInitialSyncCompleted() {
        return initialSyncCompleted.get();
    }

    public void markInitialSyncCompleted() {
        initialSyncCompleted.set(true);
    }
}
