package net.ajpappas.discord.common.command;

import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CommandRegistrar {

    private final RestClient restClient;
    private final List<GlobalCommand<?>> globalCommands;
    private final List<GuildCommand<?>> guildCommands;

    @Autowired
    public CommandRegistrar(GatewayDiscordClient client, List<GlobalCommand<?>> globalCommands, List<GuildCommand<?>> guildCommands) {
        this.restClient = client.getRestClient();
        this.globalCommands = globalCommands;
        this.guildCommands = guildCommands;
    }

    //This method will run only once on each start up and is automatically called with Spring so blocking is okay.
    @PostConstruct
    public void init() {
        final ApplicationService applicationService = restClient.getApplicationService();
        final long applicationId = restClient.getApplicationId().block();

        // Register Global Commands
        log.info("Registering {} global commands: {}", globalCommands.size(), globalCommands.stream().map(c -> c.requestBuilder().build().name()).collect(Collectors.joining(",")));
        applicationService.bulkOverwriteGlobalApplicationCommand(applicationId, globalCommands.stream().map(this::buildRequest).toList())
                .doOnNext(command -> log.debug("Successfully registered {}", command.name()))
                .doOnError(e -> log.error("Failed to register global commands", e))
                .subscribe();

        // Register Guild Commands
        log.info("Registering {} application commands: {}", guildCommands.size(), guildCommands.stream().map(c -> c.requestBuilder().build().name() + c.guilds()).collect(Collectors.joining(",")));
        guildCommands.stream()
                .flatMap(command -> command.guilds().stream()
                        .map(guild -> new AbstractMap.SimpleEntry<Long, GuildCommand<?>>(guild, command)))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue,
                                Collectors.toList())))
                .forEach((guildId, commands) ->
                        applicationService.bulkOverwriteGuildApplicationCommand(applicationId, guildId, commands.stream().map(this::buildRequest).toList())
                                .doOnNext(command -> log.debug("Successfully registered guild {} {}", guildId, command.name()))
                                .doOnError(e -> log.error("Failed to register guild {} commands", guildId, e))
                                .subscribe());

    }

    private ApplicationCommandRequest buildRequest(Command<?> command) {
        return command.requestBuilder()
                .defaultMemberPermissions(Long.toString(command.requiredPermissions().getRawValue()))
                .build();
    }
}