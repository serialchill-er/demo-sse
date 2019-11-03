package com.sseevents.util.sseeventsutil;


import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class DefaultSubscriptionRegistry implements SubscriptionRegistry {

    private final ConcurrentMap<String, Set<Client>> eventSubscribers;

    public DefaultSubscriptionRegistry() {
        this.eventSubscribers = new ConcurrentHashMap<>();
    }

    @Override
    public void subscribe(Client client, String event) {
        this.eventSubscribers.computeIfAbsent(event, k -> new HashSet<>()).add(client);
    }

    @Override
    public void unsubscribe(Client client, String event) {
        this.eventSubscribers.computeIfPresent(event,
                (k, set) -> set.remove(client) && set.isEmpty() ? null : set);

        System.out.println("Subscription Registry: "+this.eventSubscribers.toString());
    }

    @Override
    public boolean isClientSubscribedToEvent(Client client, String eventName) {
        Set<Client> subscribedClients = this.eventSubscribers.get(eventName);
        if (subscribedClients != null) {
            return subscribedClients.contains(client);
        }
        return false;
    }

    @Override
    public boolean isClientSubscribedToEvent(String clientId, String eventName) {
        List<Client> collectedList = this.eventSubscribers.computeIfAbsent(eventName, k -> new HashSet<>()).stream().filter(client -> clientId.equals(client.getId())).collect(Collectors.toList());
        return collectedList.size() != 0;
    }

    @Override
    public Set<String> getAllEvents() {
        return Collections.unmodifiableSet(this.eventSubscribers.keySet());
    }

}