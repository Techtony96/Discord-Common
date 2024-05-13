package net.ajpappas.discord.common.event;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventRegister {

    private final GatewayDiscordClient client;

    @Autowired
    public EventRegister(GatewayDiscordClient client, List<EventListener<? extends Event>> eventListeners) {
        this.client = client;
        eventListeners.forEach(this::register);
    }

    private <T extends Event> void register(EventListener<T> eventListener) {
        this.client.on(eventListener.getEventType())
                .filter(eventListener.filters())
                .filterWhen(eventListener.asyncFilters())
                .flatMap(event -> eventListener.handle(event).then().onErrorResume(eventListener::error))
                .subscribe();
    }
}
