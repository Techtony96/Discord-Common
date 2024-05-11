package net.ajpappas.discord.common.listeners;

import discord4j.core.event.domain.Event;
import net.ajpappas.discord.common.event.EventListener;
import reactor.core.publisher.Mono;

public class BadTestListener extends EventListener {
    @Override
    public Mono<Void> handle(Event event) {
        return null;
    }
}
