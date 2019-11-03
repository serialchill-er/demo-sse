package com.sseevents.util.sseeventsutil;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;

public class Client {
    private final String id;

    private boolean isExpired;

    private SseEmitter sseEmitter;

    private long lastTransfer;

    private final boolean completeAfterMessage;

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public boolean isExpired() {
        return isExpired;
    }

    Client(String id, SseEmitter sseEmitter, boolean completeAfterMessage) {
        this.id = id;
        this.isExpired=false;
        this.sseEmitter = sseEmitter;
        this.sseEmitter.onTimeout(() -> {this.isExpired=true;this.sseEmitter.complete();
            System.out.println("Timeout: "+this);});
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

    boolean isCompleteAfterMessage() {
        return this.completeAfterMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id) &&
                Objects.equals(sseEmitter, client.sseEmitter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sseEmitter);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id='" + id + '\'' +
                ", isExpired=" + isExpired +
                ", sseEmitter=" + sseEmitter +
                ", lastTransfer=" + lastTransfer +
                ", completeAfterMessage=" + completeAfterMessage +
                '}';
    }
}
