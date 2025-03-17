package com.gmail.necnionch.myplugin.switchjoin.common.platform;

import com.gmail.necnionch.myplugin.switchjoin.common.util.Message;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public interface Platform<M extends Message, S extends PlatformServer> {

    PlatformLogger getLogger();

    void broadcast(@Nullable M message);

    M formatLegacy(String message);

    String formatServerName(S server);


    Task runTaskLater(Runnable task, long delay, TimeUnit unit);

    Collection<S> getServers();


    interface Task {
        void cancel();
    }

}
