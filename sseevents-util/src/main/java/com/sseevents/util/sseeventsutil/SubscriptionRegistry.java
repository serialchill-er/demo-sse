package com.sseevents.util.sseeventsutil;

import java.util.Set;

public interface SubscriptionRegistry {
    void subscribe(String clientId, String event);

    void unsubscribe(String clientId, String event);

    boolean isClientSubscribedToEvent(String clientId, String eventName);

    /**
     * Get a collection of all registered events
     * @return an unmodifiable set of all events
     */
    Set<String> getAllEvents();

}
