package net.ajpappas.discord.common.util;

public final class StringUtil {

    public static boolean isNullOrEmpty(final String str) {
        return str == null || str.trim().isEmpty();
    }

    public static String trim(final String str) {
        return str == null ? null : str.trim();
    }
}
