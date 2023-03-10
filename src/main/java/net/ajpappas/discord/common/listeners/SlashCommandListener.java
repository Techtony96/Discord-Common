package net.ajpappas.discord.common.listeners;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
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
                .flatMap(command -> {
                            if (!hasPermission(command, event))
                                return event.reply("You do not have permission for this command.").withEphemeral(true);
                            return command.handle(event)
                                    .onErrorResume(error -> ErrorHandler.handleError(error, msg -> event.reply(msg).withEphemeral(true)));
                        }
                );
    }

    private boolean hasPermission(SlashCommand command, ChatInputInteractionEvent event) {
        if (command.requiredPermissions() == null || command.requiredPermissions().isEmpty())
            return true;

        // If this command wasn't executed in a guild, we can't check permissions
        if (Boolean.FALSE.equals(event.getInteraction().getGuild().hasElement().block())) {
            return false;
        }

        PermissionSet memberPermissions = event.getInteraction().getMember().map(Member::getBasePermissions).get().block();

        for (Permission permission : command.requiredPermissions()) {
            if (!memberPermissions.contains(permission))
                return false;
        }

        // User has all required permissions
        return true;
    }
}