package com.gmail.necnionch.myplugin.switchjoin.common.platform;

public interface PlatformLogger {

    void info(String message);

    void info(String message, Throwable exception);

    void warn(String message);

    void warn(String message, Throwable exception);

    void error(String message);

    void error(String message, Throwable exception);

}
