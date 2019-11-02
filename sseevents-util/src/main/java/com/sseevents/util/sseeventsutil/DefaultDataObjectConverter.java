package com.sseevents.util.sseeventsutil;

public class DefaultDataObjectConverter implements DataObjectConverter {

    @Override
    public boolean supports(SseEvent event) {
        return true;
    }

    @Override
    public String convert(SseEvent event) {
        if (event.data() != null) {
            return event.data().toString();
        }
        return null;
    }

}