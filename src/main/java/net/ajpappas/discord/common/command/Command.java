package net.ajpappas.discord.common.command;

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;
import discord4j.rest.util.PermissionSet;
import reactor.core.publisher.Mono;

interface Command<T extends ApplicationCommandInteractionEvent> {

    default PermissionSet requiredPermissions() {
        return PermissionSet.none();
    }

    ImmutableApplicationCommandRequest.Builder requestBuilder();

    Mono<Void> handle(ApplicationCommandInteractionEvent event);

    Class<T> getEventClassType();
}
