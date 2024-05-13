package net.ajpappas.discord.common.command;

import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import net.ajpappas.discord.common.command.Command;
import net.ajpappas.discord.common.command.GuildCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class CommandRegistrar implements ApplicationRunner {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

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
    @Override
    public void run(ApplicationArguments args) throws IOException {
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