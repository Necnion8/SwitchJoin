package com.gmail.necnionch.myplugin.switchjoin.bungee.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Cancellable;

public class SJoinAutoStopEvent extends ServerEvent implements Cancellable {

    private boolean cancelled;

    public SJoinAutoStopEvent(ServerInfo server, SwitcherServer switcherServer) {
        super(server, switcherServer);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
