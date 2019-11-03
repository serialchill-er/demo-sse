package com.sseevents.util.sseeventsutil;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultSubscriptionRegistry implements SubscriptionRegistry {

    private final ConcurrentMap<String, Set<String>> eventSubscribers;

    public DefaultSubscriptionRegistry() {
        this.eventSubscribers = new ConcurrentHashMap<>();
    }

    @Override
    public void subscribe(String clientId, String event) {
        this.eventSubscribers.computeIfAbsent(event, k -> new HashSet<>()).add(clientId);
    }

    @Override
    public void unsubscribe(String clientId, String event) {
        this.eventSubscribers.computeIfPresent(event,
                (k, set) -> set.remove(clientId) && set.isEmpty() ? null : set);
    }

    @Override
    public boolean isClientSubscribedToEvent(String clientId, String eventName) {
        Set<String> subscribedClients = this.eventSubscribers.get(eventName);
        if (subscribedClients != null) {
            return subscribedClients.contains(clientId);
        }
        return false;
    }

    @Override
    public Set<String> getAllEvents() {
        return Collections.unmodifiableSet(this.eventSubscribers.keySet());
    }

}