package net.ajpappas.discord.common.listeners;

import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.message.MessageCreateEvent;
import net.ajpappas.discord.common.event.EventListener;
import net.ajpappas.discord.common.util.EventFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.function.Predicate;

@Component
public class TestListener extends EventListener<MessageCreateEvent> {

    @Override
    public Predicate<MessageCreateEvent> filters() {
        return EventFilters.NO_BOTS
                .and(e -> "hello".equalsIgnoreCase(e.getMessage().getContent()));
    }

    @Override
    public Mono<Void> handle(MessageCreateEvent event) {
        return null;
    }
}
