package net.ajpappas.discord.common.util;

import discord4j.core.event.domain.message.MessageCreateEvent;

import java.util.function.Predicate;

public final class EventFilters {

    public static final Predicate<MessageCreateEvent> NO_BOTS = event -> event.getMessage().getAuthor().map(user -> !user.isBot()).orElse(false);

}
