package net.ajpappas.discord.common.commands;

import discord4j.common.util.TimestampFormat;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import net.ajpappas.discord.common.command.GlobalSlashCommand;
import net.ajpappas.discord.common.model.exception.UserException;
import net.ajpappas.discord.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
public class VersionCommand implements GlobalSlashCommand {

    @Value("${git.commit.id.describe-short:#{null}}")
    private String version;

    @Value("${git.commit.time:#{null}}")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Instant commitTime;

    @Override
    public PermissionSet requiredPermissions() {
        return PermissionSet.of(Permission.ADMINISTRATOR);
    }

    @Override
    public ImmutableApplicationCommandRequest.Builder requestBuilder() {
        return ApplicationCommandRequest.builder()
                .name("version")
                .description("Get the current version of the bot");
    }

    @Override
    public Mono<Void> handle(ApplicationCommandInteractionEvent event) {
        if (StringUtil.isNullOrEmpty(version) && commitTime == null)
            return Mono.error(new UserException("Version information is not available."));
        return event.reply().withEphemeral(true).withContent(String.format("Version: `%s`\nDate: %s", version, getFormattedCommitTime()));
    }

    @Override
    public Class<ChatInputInteractionEvent> getEventClassType() {
        return ChatInputInteractionEvent.class;
    }

    private String getFormattedCommitTime() {
        return String.format("%s (%s)", TimestampFormat.SHORT_DATE_TIME.format(commitTime), TimestampFormat.RELATIVE_TIME.format(commitTime));
    }
}
