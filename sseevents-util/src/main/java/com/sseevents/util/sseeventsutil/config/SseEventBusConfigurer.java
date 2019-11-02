package com.sseevents.util.sseeventsutil.config;

import com.sseevents.util.sseeventsutil.ClientEvent;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

public interface SseEventBusConfigurer {

    /**
     * Specifies the delay between runs of the internal error queue job. <br>
     * This job tries to re-submits failed sent events.
     * <p>
     * Default: 500 milliseconds
     */
    default Duration schedulerDelay() {
        return Duration.ofMillis(500);
    }

    /**
     * Specifies the delay between runs of the internal job that checks for expired
     * clients.<br>
     * <p>
     * Default: {@link #clientExpiration()} (1 day)
     */
    default Duration clientExpirationJobDelay() {
        return clientExpiration();
    }

    /**
     * Duration after the last successful data connection, a client is removed from the
     * internal registry.
     * <p>
     * Default: 1 day
     */
    default Duration clientExpiration() {
        return Duration.ofDays(1);
    }

    /**
     * Number of tries to send an event. When the event cannot be send that many times it
     * will be removed from the internal registry.
     */
    default int noOfSendResponseTries() {
        return 40;
    }

    /**
     * An executor that schedules and runs the internal jobs
     * <p>
     * By default this is an instance created with
     * {@link Executors#newScheduledThreadPool(2)}
     */
    default ScheduledExecutorService taskScheduler() {
        return Executors.newScheduledThreadPool(2);
    }

    default BlockingQueue<ClientEvent> errorQueue() {
        return new LinkedBlockingQueue<>();
    }

    default BlockingQueue<ClientEvent> sendQueue() {
        return new LinkedBlockingQueue<>();
    }

}