package com.sseevents.util.sseeventsutil;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class Client {
    private final String id;

    private SseEmitter sseEmitter;

    private long lastTransfer;

    private final boolean completeAfterMessage;

    Client(String id, SseEmitter sseEmitter, boolean completeAfterMessage) {
        this.id = id;
        this.sseEmitter = sseEmitter;
        this.lastTransfer = System.currentTimeMillis();
        this.completeAfterMessage = completeAfterMessage;
    }

    public String getId() {
        return this.id;
    }

    long lastTransfer() {
        return this.lastTransfer;
    }

    void updateLastTransfer() {
        this.lastTransfer = System.currentTimeMillis();
    }

    SseEmitter sseEmitter() {
        return this.sseEmitter;
    }

    void updateEmitter(SseEmitter emitter) {
        this.sseEmitter = emitter;
    }

    boolean isCompleteAfterMessage() {
        return this.completeAfterMessage;
    }

}
