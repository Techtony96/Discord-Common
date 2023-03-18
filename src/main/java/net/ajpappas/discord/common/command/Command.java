package net.ajpappas.discord.common.command;

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.rest.util.PermissionSet;
import reactor.core.publisher.Mono;

public interface Command<T extends ApplicationCommandInteractionEvent> {

    String getName();

    default PermissionSet requiredPermissions() {
        return PermissionSet.none();
    }

    Mono<Void> handle(T event);

}
