package net.ajpappas.discord.common.command;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import lombok.extern.log4j.Log4j2;
import net.ajpappas.discord.common.event.EventListener;
import net.ajpappas.discord.common.util.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Log4j2
public class CommandListener implements EventListener<ApplicationCommandInteractionEvent> {

    private final List<Command<?>> commands;

    @Autowired
    public CommandListener(GatewayDiscordClient client, List<Command<?>> commands) {
        this.commands = commands;
    }

    @Override
    public Class<ApplicationCommandInteractionEvent> getEventType() {
        return ApplicationCommandInteractionEvent.class;
    }

    @Override
    public Mono<Void> handle(ApplicationCommandInteractionEvent event) {
        return Flux.fromIterable(commands)
                .filter(command -> command.requestBuilder().build().name().equals(event.getCommandName()))
                .next()
                .flatMap(command -> {
                    if (!hasPermission(command, event)) {
                        return event.reply("You do not have permission for this command.").withEphemeral(true);
                    }
                    return command.handle(command.getEventClassType().cast(event))
                            .onErrorResume(error -> ErrorHandler.handleError(error, msg -> event.reply(msg).withEphemeral(true)));
                });
    }

    private boolean hasPermission(Command<? extends ApplicationCommandInteractionEvent> command, ApplicationCommandInteractionEvent event) {
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