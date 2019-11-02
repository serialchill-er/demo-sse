package com.sseevents.util.sseeventsutil;

import org.immutables.value.Value;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Value.Style(depluralize = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
@Value.Immutable
public interface SseEvent {

    public static String DEFAULT_EVENT = "message";

    Set<String> clientIds();

    /**
     * Is ignored when clientIds is not empty
     */
    Set<String> excludeClientIds();

    Optional<Class<?>> jsonView();

    @Value.Default
    default String event() {
        return DEFAULT_EVENT;
    }

    Object data();

    Optional<Duration> retry();

    Optional<String> id();

    Optional<String> comment();

    /**
     * Creates a SseEvent that just contains the data. The data will be converted when
     * it's not a String instance.
     */
    public static SseEvent ofData(Object data) {
        return SseEvent.builder().data(data).build();
    }

    /**
     * Creates a SseEvent that contains an event and an empty string
     */
    public static SseEvent ofEvent(String event) {
        return SseEvent.builder().event(event).data("").build();
    }

    /**
     * Creates a SseEvent that just contains an event and data. The data will be converted
     * when it's not a String instance
     */
    public static SseEvent of(String event, Object data) {
        return SseEvent.builder().event(event).data(data).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ImmutableSseEvent.Builder {

        // nothing here

    }

}

