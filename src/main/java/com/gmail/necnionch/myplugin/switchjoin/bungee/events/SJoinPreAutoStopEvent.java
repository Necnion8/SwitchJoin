package com.gmail.necnionch.myplugin.switchjoin.bungee.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.Nullable;

public class SJoinPreAutoStopEvent extends BroadcastServerEvent {
    private final Integer remainingMinutes;

    public SJoinPreAutoStopEvent(ServerInfo server, SwitcherServer switcherServer, @Nullable BaseComponent[] broadcastMessage, Integer remainingMinutes) {
        super(server, switcherServer, broadcastMessage);
        this.remainingMinutes = remainingMinutes;
    }

    public Integer getRemainingMinutes() {
        return remainingMinutes;
    }

}
