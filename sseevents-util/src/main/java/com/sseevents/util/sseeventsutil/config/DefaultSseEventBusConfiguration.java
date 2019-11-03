package com.sseevents.util.sseeventsutil.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sseevents.util.sseeventsutil.DataObjectConverter;
import com.sseevents.util.sseeventsutil.DefaultDataObjectConverter;
import com.sseevents.util.sseeventsutil.DefaultSubscriptionRegistry;
import com.sseevents.util.sseeventsutil.JacksonDataObjectConverter;
import com.sseevents.util.sseeventsutil.SseEventBus;
import com.sseevents.util.sseeventsutil.SubscriptionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DefaultSseEventBusConfiguration {
    private SseEventBusConfigurer configurer;

    private ObjectMapper objectMapper;

    private List<DataObjectConverter> dataObjectConverters;

    private SubscriptionRegistry subscriptionRegistry;

    public DefaultSseEventBusConfiguration(){
        this.configurer=new SseEventBusConfigurer() {};
        this.objectMapper=new ObjectMapper();
        ArrayList<DataObjectConverter> converters = new ArrayList<>();
        converters.add(new JacksonDataObjectConverter(this.objectMapper));
        converters.add(new DefaultDataObjectConverter());
        this.dataObjectConverters=converters;
        this.subscriptionRegistry=new DefaultSubscriptionRegistry();
    }

    @Bean
    public SseEventBus eventBus() {
        SseEventBus sseEventBus = new SseEventBus(this.configurer, this.subscriptionRegistry);

        sseEventBus.setDataObjectConverters(this.dataObjectConverters);

        return sseEventBus;
    }

}