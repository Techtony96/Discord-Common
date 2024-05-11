package net.ajpappas.discord.common.command;

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;
import discord4j.rest.util.PermissionSet;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

interface Command<T extends ApplicationCommandInteractionEvent> {

    default PermissionSet requiredPermissions() {
        return PermissionSet.none();
    }

    ImmutableApplicationCommandRequest.Builder requestBuilder();

    Mono<Void> handle(ApplicationCommandInteractionEvent event);

    default Class<T> getEventClassType() {
        Type superClass = getClass().getGenericSuperclass();
        return (Class<T>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

}
