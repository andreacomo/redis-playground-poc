package it.condingjam.redis_playground_poc.commons;

import org.springframework.stereotype.Component;

@Component
public class RunningStatus {

    private volatile boolean running;

    public RunningStatus() {
        run();
    }

    public void run() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
