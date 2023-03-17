package net.ajpappas.discord.common.command;

import discord4j.rest.util.PermissionSet;

public interface Command {

    String getName();

    default PermissionSet requiredPermissions() {
        return PermissionSet.none();
    }

}
