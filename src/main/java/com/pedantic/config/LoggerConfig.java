package com.pedantic.config;

import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Qualifier;
import jakarta.ws.rs.Produces;
import java.util.logging.Logger;

public class LoggerConfig {

    @Produces
    public Logger produceLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getSimpleName());
    }
}
