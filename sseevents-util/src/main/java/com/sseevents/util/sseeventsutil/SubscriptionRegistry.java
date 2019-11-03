package com.sseevents.util.sseeventsutil;

import java.util.Set;

public interface SubscriptionRegistry {
    void subscribe(Client client, String event);

    void unsubscribe(Client client, String event);

    boolean isClientSubscribedToEvent(Client client, String eventName);

    boolean isClientSubscribedToEvent(String clientId, String eventName);

    Set<String> getAllEvents();

}
