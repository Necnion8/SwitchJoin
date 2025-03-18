package com.gmail.necnionch.myplugin.switchjoin.velocity.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

public class SJoinAutoStopEvent extends ServerEvent {

    private boolean cancelled;

    public SJoinAutoStopEvent(RegisteredServer server, SwitcherServer switcherServer) {
        super(server, switcherServer);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
