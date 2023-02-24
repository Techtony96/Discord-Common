package net.ajpappas.discord.common.listeners;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import net.ajpappas.discord.common.command.SlashCommand;
import net.ajpappas.discord.common.util.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Component
public class SlashCommandListener {

    private final Collection<SlashCommand> commands;

    @Autowired
    public SlashCommandListener(List<SlashCommand> slashCommands, GatewayDiscordClient client) {
        this.commands = slashCommands;
        client.on(ChatInputInteractionEvent.class, this::handle).subscribe();
    }


    public Mono<Void> handle(ChatInputInteractionEvent event) {
        return Flux.fromIterable(commands)
            .filter(command -> command.getName().equals(event.getCommandName()))
            .next()
            .flatMap(command -> command.handle(event)
                    .onErrorResume(error -> ErrorHandler.handleError(error, msg -> event.reply(msg).withEphemeral(true)))
            );
    }
}