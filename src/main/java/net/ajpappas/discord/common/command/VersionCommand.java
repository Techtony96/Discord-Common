package net.ajpappas.discord.common.command;

import discord4j.common.util.TimestampFormat;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import net.ajpappas.discord.common.exception.UserException;
import net.ajpappas.discord.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
public class VersionCommand implements SlashCommand {

    @Value("${git.commit.id.describe-short:#{null}}")
    private String version;

    @Value("${git.commit.time:#{null}}")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Instant commitTime;

    @Override
    public String getName() {
        return "version";
    }

    @Override
    public PermissionSet requiredPermissions() {
        return PermissionSet.of(Permission.ADMINISTRATOR);
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        if (StringUtil.isNullOrEmpty(version) && commitTime == null)
            return Mono.error(new UserException("Version information is not available."));
        return event.reply().withEphemeral(true).withContent(String.format("Version: `%s`\nDate: %s", version, getFormattedCommitTime()));
    }

    private String getFormattedCommitTime() {
        return String.format("%s (%s)", TimestampFormat.SHORT_DATE_TIME.format(commitTime), TimestampFormat.RELATIVE_TIME.format(commitTime));
    }
}
