package com.sseevents.util.sseeventsutil;

public interface DataObjectConverter {
    boolean supports(SseEvent event);

    String convert(SseEvent event);

}
