/**
 * Copyright 2016-2018 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sseevents.util.sseeventsutil;

import com.sseevents.util.sseeventsutil.config.SseEventBusConfigurer;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SseEventBus {

    private final ConcurrentMap<String, Set<Client>> clients;

    private final SubscriptionRegistry subscriptionRegistry;

    private final ScheduledExecutorService taskScheduler;

    private final int noOfSendResponseTries;

    private DataObjectConverter dataObjectConverter;

    private final BlockingQueue<ClientEvent> errorQueue;

    private final BlockingQueue<ClientEvent> sendQueue;

    public SseEventBus(SseEventBusConfigurer configurer,
                       SubscriptionRegistry subscriptionRegistry) {

        this.subscriptionRegistry = subscriptionRegistry;

        this.taskScheduler = configurer.taskScheduler();
        this.noOfSendResponseTries = configurer.noOfSendResponseTries();

        this.clients = new ConcurrentHashMap<>();

        this.errorQueue = configurer.errorQueue();
        this.sendQueue = configurer.sendQueue();

        this.taskScheduler.submit(this::eventLoop);
        this.taskScheduler.scheduleWithFixedDelay(this::reScheduleFailedEvents, 0,
                configurer.schedulerDelay().toMillis(), TimeUnit.MILLISECONDS);
        this.taskScheduler.scheduleWithFixedDelay(this::cleanUpClients, 0,
                configurer.clientExpirationJobDelay().toMillis(), TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void cleanUp() {
        this.taskScheduler.shutdownNow();
    }

    public SseEmitter createSseEmitter(String clientId, String... events) {
        return createSseEmitter(clientId, 180_000L, false, false, events);
    }

    public SseEmitter createSseEmitter(String clientId, Long timeout, boolean unsubscribe,
                                       String... events) {
        return createSseEmitter(clientId, timeout, unsubscribe, false, events);
    }

    public SseEmitter createSseEmitter(String clientId, Long timeout, boolean unsubscribe,
                                       boolean completeAfterMessage, String... events) {
        SseEmitter emitter = new SseEmitter(timeout);
        Client registeredClient = registerClient(clientId, emitter, completeAfterMessage);

        if (events != null && events.length > 0) {
            if (unsubscribe) {
                unsubscribeFromAllEvents(registeredClient, events);
            }
            for (String event : events) {
                subscribe(registeredClient, event);
            }
        }

        return emitter;
    }

    public void registerClient(String clientId, SseEmitter emitter) {
        this.registerClient(clientId, emitter, false);
    }

    public Client registerClient(String clientId, SseEmitter emitter,
                               boolean completeAfterMessage) {
        Set<Client> clientSet = this.clients.get(clientId);
        Client newClient = new Client(clientId, emitter, completeAfterMessage);
        if (clientSet == null) {
            HashSet<Client> newClientSet = new HashSet<>();
            newClientSet.add(newClient);
            this.clients.put(clientId, newClientSet);
        } else {
            if (!clientSet.contains(newClient)) {  //TODO Can remove if statement
                clientSet.add(newClient);
            }
        }
        return newClient;
    }


    public void unregisterClient(Client client) {
        unsubscribeFromAllEvents(client);
        this.clients.get(client.getId()).remove(client);
        System.out.println("Unregistering: "+client);
        System.out.println("client->emitter: "+this.clients.toString());
//        System.out.println("Unregister: "+client.getId());
    }

    public void subscribe(Client client, String event) {
        this.subscriptionRegistry.subscribe(client, event);
    }

    public void unsubscribe(Client client, String event) {
        this.subscriptionRegistry.unsubscribe(client, event);
    }

    public void unsubscribeFromAllEvents(Client client, String... keepEvents) {
        Set<String> keepEventsSet = null;
        if (keepEvents != null && keepEvents.length > 0) {
            keepEventsSet = new HashSet<>();
            for (String keepEvent : keepEvents) {
                keepEventsSet.add(keepEvent);
            }
        }

        Set<String> events = this.subscriptionRegistry.getAllEvents();
        if (keepEventsSet != null) {
            events = new HashSet<>(events);
            events.removeAll(keepEventsSet);
        }
        System.out.println("Events: "+events.size() +" : "+events);
        events.forEach(event -> unsubscribe(client, event));
    }

    @EventListener
    public void handleEvent(SseEvent event) {
            String convertedValue = null;
            if (!(event.data() instanceof String)) {
                convertedValue = this.convertObject(event);
            }
            String finalConvertedValue = convertedValue;

            if (event.clientIds().isEmpty()) {
                List<Client> clientList = this.clients.values().stream().flatMap(Collection::stream).collect(Collectors.toList());

                clientList.stream().filter(client -> (!event.excludeClientIds().contains(client.getId())
                        && this.subscriptionRegistry.isClientSubscribedToEvent(
                        client, event.event()))).forEach(client -> {
                    try {
                        this.sendQueue
                                .put(new ClientEvent(client, event, finalConvertedValue));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                for (String clientId : event.clientIds()) {
                    if (this.subscriptionRegistry.isClientSubscribedToEvent(clientId,
                            event.event())) {

                        this.clients.get(clientId).forEach(client -> {
                            try {
                                this.sendQueue.put(new ClientEvent(client,
                                        event, finalConvertedValue));
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                }
            }
    }

    private void reScheduleFailedEvents() {
        List<ClientEvent> failedEvents = new ArrayList<>();
        this.errorQueue.drainTo(failedEvents);

        for (ClientEvent sseClientEvent : failedEvents) {
            if (this.subscriptionRegistry.isClientSubscribedToEvent(
                    sseClientEvent.getClient(),
                    sseClientEvent.getSseEvent().event())) {
                try {
                    this.sendQueue.put(sseClientEvent);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void eventLoop() {
        try {
            while (true) {
                ClientEvent clientEvent = this.sendQueue.take();
                if (clientEvent.getErrorCounter() < this.noOfSendResponseTries) {
                    Client client = clientEvent.getClient();
                    boolean ok = sendEventToClient(clientEvent);
                    if (ok) {
                        client.updateLastTransfer();
                    } else {
                        clientEvent.incErrorCounter();
                        this.errorQueue.put(clientEvent);
                    }
                } else {
                    System.out.println("From Event Loop : "+clientEvent.getErrorCounter()+ " : "+Thread.currentThread().getName());
                    clientEvent.getClient().setExpired(true);
                    clientEvent.getClient().sseEmitter().complete();
//                    this.unregisterClient(clientEvent.getClient());
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean sendEventToClient(ClientEvent clientEvent) {
        Client client = clientEvent.getClient();
        try {
            client.sseEmitter().send(clientEvent.createSseEventBuilder());
            if (client.isCompleteAfterMessage()) {
                client.setExpired(true);
                client.sseEmitter().complete();
            }
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    private String convertObject(SseEvent event) {
        if (this.dataObjectConverter != null) {
                if (this.dataObjectConverter.supports(event)) {
                    return this.dataObjectConverter.convert(event);
                }
        }
        return null;
    }

    private void cleanUpClients() {
        if (!this.clients.isEmpty()) {
            Iterator<Entry<String, Set<Client>>> it = this.clients.entrySet().iterator();
            Set<Client> staleClients = new HashSet<>();
            while (it.hasNext()) {
                Entry<String, Set<Client>> entry = it.next();
                entry.getValue().stream().filter(Client::isExpired).forEach(staleClients::add);
            }
            System.out.println("From cleanUpClients");
            System.out.println("Stale Clients:"+staleClients);
            staleClients.forEach(this::unregisterClient);
        }
    }

    public void setDataObjectConverters(DataObjectConverter dataObjectConverter) {
        this.dataObjectConverter = dataObjectConverter;
    }

}
