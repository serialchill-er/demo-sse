package com.sseevents.util.sseeventsutil;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;

public class ClientEvent {

    private final Client client;

    private final SseEvent event;

    private final String convertedValue;

    private int errorCounter;

    public ClientEvent(Client client, SseEvent event, String convertedValue) {
        this.client = client;
        this.event = event;
        this.convertedValue = convertedValue;
        this.errorCounter = 0;
    }

    public Client getClient() {
        return this.client;
    }

    public SseEvent getSseEvent() {
        return this.event;
    }

    public SseEmitter.SseEventBuilder createSseEventBuilder() {

        SseEmitter.SseEventBuilder sseBuilder = SseEmitter.event();

        if (!this.event.event().equals(SseEvent.DEFAULT_EVENT)) {
            sseBuilder.name(this.event.event());
        }

        this.event.id().ifPresent(sseBuilder::id);
        this.event.retry().map(Duration::toMillis).ifPresent(sseBuilder::reconnectTime);
        this.event.comment().ifPresent(sseBuilder::comment);

        if (this.convertedValue != null) {
            for (String line : this.convertedValue.split("\n")) {
                sseBuilder.data(line);
            }
        }
        else if (this.event.data() instanceof String) {
            for (String line : ((String) this.event.data()).split("\n")) {
                sseBuilder.data(line);
            }
        }
        else {
            sseBuilder.data(this.event.data());
        }

        return sseBuilder;

    }

    public void incErrorCounter() {
        this.errorCounter++;
    }

    public int getErrorCounter() {
        return this.errorCounter;
    }
}
