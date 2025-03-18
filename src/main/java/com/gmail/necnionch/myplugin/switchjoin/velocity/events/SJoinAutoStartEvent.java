package com.gmail.necnionch.myplugin.switchjoin.velocity.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class SJoinAutoStartEvent extends BroadcastServerEvent {
    private boolean cancelled;

    public SJoinAutoStartEvent(RegisteredServer server, SwitcherServer switcherServer, @Nullable Component broadcastMessage) {
        super(server, switcherServer, broadcastMessage);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
