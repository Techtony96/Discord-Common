package net.ajpappas.discord.common.util;

import net.ajpappas.discord.common.model.exception.UserException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public final class ErrorHandler {
    private static final Logger log = LogManager.getLogger();

    public static Mono<Void> handleError(Throwable error, Function<String, Mono> reply) {
        if (error instanceof UserException) {
            return reply.apply(error.getMessage());
        } else {
            log.error(error);
            return reply.apply("Unknown error.");
        }
    }
}