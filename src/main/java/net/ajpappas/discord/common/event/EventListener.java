package net.ajpappas.discord.common.event;

import discord4j.core.event.domain.Event;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

public abstract class EventListener<T extends Event> {

    @Getter
    private final Class<T> eventType;

    public EventListener(Class<T> eventType) {
        this.eventType = eventType;
    }

    public Predicate<? extends T> filters() {
        return x -> true;
    }

    public abstract Mono<Void> handle(T event);

    public Class<T> getEventType() {
        return eventType;
    }
}
