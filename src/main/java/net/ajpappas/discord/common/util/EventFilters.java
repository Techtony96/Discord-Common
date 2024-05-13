package net.ajpappas.discord.common.util;

import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;

public final class EventFilters {

    public static final Predicate<MessageCreateEvent> NO_BOTS = event -> event.getMessage().getAuthor().map(user -> !user.isBot()).orElse(false);

    public static final Predicate<Event> NO_FILTER = x -> true;

    public static final Function<? super Event,? extends Publisher<Boolean>> ASYNC_NO_FILTER = x -> Mono.just(true);
}
