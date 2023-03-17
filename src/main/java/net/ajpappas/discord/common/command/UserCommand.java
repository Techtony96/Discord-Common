package net.ajpappas.discord.common.command;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.UserInteractionEvent;
import discord4j.rest.util.PermissionSet;
import reactor.core.publisher.Mono;

public interface UserCommand extends Command {

    Mono<Void> handle(UserInteractionEvent event);
}
