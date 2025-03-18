package com.gmail.necnionch.myplugin.switchjoin.velocity.platform;

import com.gmail.necnionch.myplugin.switchjoin.common.platform.PlatformLogger;
import org.slf4j.Logger;


public class VelocityLogger implements PlatformLogger {

    private final Logger log;

    public VelocityLogger(Logger logger) {
        this.log = logger;
    }

    @Override
    public void info(String message) {
        log.info(message);
    }

    @Override
    public void info(String message, Throwable exception) {
        log.info(message, exception);
    }

    @Override
    public void warn(String message) {
        log.warn(message);
    }

    @Override
    public void warn(String message, Throwable exception) {
        log.warn(message, exception);
    }

    @Override
    public void error(String message) {
        log.error(message);
    }

    @Override
    public void error(String message, Throwable exception) {
        log.error(message, exception);
    }
}