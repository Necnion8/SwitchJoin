package com.gmail.necnionch.myplugin.switchjoin.bungee.platform;

import com.gmail.necnionch.myplugin.switchjoin.common.platform.PlatformLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BungeeLogger implements PlatformLogger {

    private final Logger log;

    public BungeeLogger(Logger logger) {
        this.log = logger;
    }

    @Override
    public void info(String message) {
        log.info(message);
    }

    @Override
    public void info(String message, Throwable exception) {
        log.log(Level.INFO, message, exception);
    }

    @Override
    public void warn(String message) {
        log.warning(message);
    }

    @Override
    public void warn(String message, Throwable exception) {
        log.log(Level.WARNING, message, exception);
    }

    @Override
    public void error(String message) {
        log.severe(message);
    }

    @Override
    public void error(String message, Throwable exception) {
        log.log(Level.SEVERE, message, exception);
    }
}