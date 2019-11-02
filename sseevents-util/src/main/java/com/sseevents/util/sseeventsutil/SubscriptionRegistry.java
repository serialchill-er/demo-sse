package com.sseevents.util.sseeventsutil;

import java.util.Map;
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

    /**
     * Get a map that maps events to a collection of clientIds
     * @return map with the event as key, the value is a set of clientIds
     */
    Map<String, Set<String>> getAllSubscriptions();

    /**
     * Get all subscribers to a particular event
     * @return an unmodifiable set of all subscribed clientIds to this event. Empty when
     * nobody is subscribed
     */
    Set<String> getSubscribers(String event);

    /**
     * Get the number of subscribers to a particular event
     * @return the number of clientIds subscribed to this event. 0 when nobody is
     * subscribed
     */
    int countSubscribers(String event);

    /**
     * Check if a particular event has subscribers
     * @return true when the event has 1 or more subscribers.
     */
    boolean hasSubscribers(String event);
}
