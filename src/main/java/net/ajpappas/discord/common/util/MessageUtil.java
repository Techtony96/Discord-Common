package net.ajpappas.discord.common.util;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;

public final class MessageUtil {

    public static String getLink(Message message) {
        String guildId = message.getGuildId().map(Snowflake::asString).orElse("@me");
        String channelId = message.getChannelId().asString();
        String messageId = message.getId().asString();
        return String.format("https://discord.com/channels/%s/%s/%s", guildId, channelId, messageId);
    }

    private String getChannelLink(Message message) {
        String guildId = message.getGuildId().map(Snowflake::asString).orElse("@me");
        String channelId = message.getChannelId().asString();
        return String.format("https://discord.com/channels/%s/%s", guildId, channelId);
    }
}
