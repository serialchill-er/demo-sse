package com.sseevents.util.sseeventsutil.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sseevents.util.sseeventsutil.DataObjectConverter;
import com.sseevents.util.sseeventsutil.DefaultSubscriptionRegistry;
import com.sseevents.util.sseeventsutil.JacksonDataObjectConverter;
import com.sseevents.util.sseeventsutil.SseEventBus;
import com.sseevents.util.sseeventsutil.SubscriptionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultSseEventBusConfiguration {
    private SseEventBusConfigurer configurer;

    private ObjectMapper objectMapper;

    private DataObjectConverter dataObjectConverter;

    private SubscriptionRegistry subscriptionRegistry;

    public DefaultSseEventBusConfiguration(){
        this.configurer=new SseEventBusConfigurer() {};
        this.objectMapper=new ObjectMapper();
        DataObjectConverter converter = new JacksonDataObjectConverter(this.objectMapper);
        this.dataObjectConverter=converter;
        this.subscriptionRegistry=new DefaultSubscriptionRegistry();
    }

    @Bean
    public SseEventBus eventBus() {
        SseEventBus sseEventBus = new SseEventBus(this.configurer, this.subscriptionRegistry);

        sseEventBus.setDataObjectConverters(this.dataObjectConverter);

        return sseEventBus;
    }

}