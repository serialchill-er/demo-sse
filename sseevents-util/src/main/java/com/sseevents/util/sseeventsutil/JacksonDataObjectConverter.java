package com.sseevents.util.sseeventsutil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonDataObjectConverter implements DataObjectConverter {

    private final ObjectMapper objectMapper;

    public JacksonDataObjectConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(SseEvent event) {
        return true;
    }

    @Override
    public String convert(SseEvent event) {
        if (event.data() != null) {
            try {
                if (!event.jsonView().isPresent()) {
                    return this.objectMapper.writeValueAsString(event.data());
                }

                return this.objectMapper.writerWithView(event.jsonView().get())
                        .writeValueAsString(event.data());
            }
            catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}