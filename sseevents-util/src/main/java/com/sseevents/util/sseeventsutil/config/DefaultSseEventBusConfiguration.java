package com.sseevents.util.sseeventsutil.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sseevents.util.sseeventsutil.DataObjectConverter;
import com.sseevents.util.sseeventsutil.DefaultDataObjectConverter;
import com.sseevents.util.sseeventsutil.DefaultSubscriptionRegistry;
import com.sseevents.util.sseeventsutil.JacksonDataObjectConverter;
import com.sseevents.util.sseeventsutil.SseEventBus;
import com.sseevents.util.sseeventsutil.SubscriptionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DefaultSseEventBusConfiguration {

    @Autowired(required = false)
    protected SseEventBusConfigurer configurer;

    @Autowired(required = false)
    protected ObjectMapper objectMapper;

    @Autowired(required = false)
    protected List<DataObjectConverter> dataObjectConverters;

    @Autowired(required = false)
    protected SubscriptionRegistry subscriptionRegistry;

    @Bean
    public SseEventBus eventBus() {
        SseEventBusConfigurer config = this.configurer;
        if (config == null) {
            config = new SseEventBusConfigurer() {
                /* nothing_here */ };
        }

        SubscriptionRegistry registry = this.subscriptionRegistry;
        if (registry == null) {
            registry = new DefaultSubscriptionRegistry();
        }

        SseEventBus sseEventBus = new SseEventBus(config, registry);

        List<DataObjectConverter> converters = this.dataObjectConverters;
        if (converters == null) {
            converters = new ArrayList<>();
        }

        if (this.objectMapper != null) {
            converters.add(new JacksonDataObjectConverter(this.objectMapper));
        }
        else {
            converters.add(new DefaultDataObjectConverter());
        }

        sseEventBus.setDataObjectConverters(converters);

        return sseEventBus;
    }

}