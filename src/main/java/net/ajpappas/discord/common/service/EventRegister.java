package net.ajpappas.discord.common.service;

import com.austinv11.servicer.Service;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import net.ajpappas.discord.common.event.EventListener;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.function.Predicate3;

import java.util.List;
import java.util.function.Predicate;

@Service
public class EventRegister {

    private final GatewayDiscordClient client;

    @Autowired
    public EventRegister(GatewayDiscordClient client, List<EventListener<? extends Event>> eventListeners) {
        this.client = client;
        eventListeners.forEach(this::register);
    }

    private void register(EventListener<? extends Event> eventListener) {
        this.client.on(eventListener.getEventType())
              //  .filter(eventListener.filters())
                .doOnNext(eventListener::handle)
                .subscribe();
    }
}
