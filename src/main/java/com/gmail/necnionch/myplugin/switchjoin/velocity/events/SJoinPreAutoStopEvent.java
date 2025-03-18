package com.gmail.necnionch.myplugin.switchjoin.velocity.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class SJoinPreAutoStopEvent extends BroadcastServerEvent {
    private final Integer remainingMinutes;

    public SJoinPreAutoStopEvent(RegisteredServer server, SwitcherServer switcherServer, @Nullable Component broadcastMessage, Integer remainingMinutes) {
        super(server, switcherServer, broadcastMessage);
        this.remainingMinutes = remainingMinutes;
    }

    public Integer getRemainingMinutes() {
        return remainingMinutes;
    }

}
