package net.ajpappas.discord.common.listeners;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.event.domain.interaction.UserInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import net.ajpappas.discord.common.command.Command;
import net.ajpappas.discord.common.command.MessageCommand;
import net.ajpappas.discord.common.command.SlashCommand;
import net.ajpappas.discord.common.command.UserCommand;
import net.ajpappas.discord.common.util.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Component
public class CommandListener {

    private final Collection<SlashCommand> slashCommands;
    private final Collection<MessageCommand> messageCommands;
    private final Collection<UserCommand> userCommands;

    @Autowired
    public CommandListener(List<SlashCommand> slashCommands, List<MessageCommand> messageCommands, List<UserCommand> userCommands, GatewayDiscordClient client) {
        this.slashCommands = slashCommands;
        this.messageCommands = messageCommands;
        this.userCommands = userCommands;
        client.on(ChatInputInteractionEvent.class, this::slashCommand).subscribe();
        client.on(MessageInteractionEvent.class, this::messageCommand).subscribe();
        client.on(UserInteractionEvent.class, this::userCommand).subscribe();
    }


    public Mono<Void> slashCommand(ChatInputInteractionEvent event) {
        return Flux.fromIterable(slashCommands)
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

    public Mono<Void> messageCommand(MessageInteractionEvent event) {
        return Flux.fromIterable(messageCommands)
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

    public Mono<Void> userCommand(UserInteractionEvent event) {
        return Flux.fromIterable(userCommands)
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

    private boolean hasPermission(Command command, ApplicationCommandInteractionEvent event) {
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