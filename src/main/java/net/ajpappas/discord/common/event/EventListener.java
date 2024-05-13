package net.ajpappas.discord.common.event;

import discord4j.core.event.domain.Event;
import lombok.extern.log4j.Log4j2;
import net.ajpappas.discord.common.util.EventFilters;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;

public interface EventListener<T extends Event> {

    @Log4j2
    final class LogHolder{}

    Class<T> getEventType();

    default Predicate<? super T> filters() {
        return EventFilters.NO_FILTER;
    }

    default Function<? super T, ? extends Publisher<Boolean>> asyncFilters() {
        return EventFilters.ASYNC_NO_FILTER;
    }

    Mono<Void> handle(T event);

    default Mono<Void> error(Throwable throwable) {
        LogHolder.log.error("Unable to handle {} event", getEventType(), throwable);
        return Mono.empty();
    }
}
