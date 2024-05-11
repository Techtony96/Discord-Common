package net.ajpappas.discord.common.command;

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;

import java.util.Collection;

interface GuildCommand<T extends ApplicationCommandInteractionEvent> extends Command<T> {

    Collection<Long> guilds();

}
