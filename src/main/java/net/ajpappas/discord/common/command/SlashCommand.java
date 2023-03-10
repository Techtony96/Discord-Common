package net.ajpappas.discord.common.command;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.rest.util.PermissionSet;
import reactor.core.publisher.Mono;

public interface SlashCommand {

    String getName();

    Mono<Void> handle(ChatInputInteractionEvent event);

    default PermissionSet requiredPermissions() {
        return PermissionSet.none();
    }
}
