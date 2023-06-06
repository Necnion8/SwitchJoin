package com.gmail.necnionch.myplugin.switchjoin.bungee.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class SJoinAutoStopEvent extends Event implements Cancellable {
    private boolean cancelled = false;
    private final ServerInfo serverInfo;
    private final SwitcherServer switcherServer;

    public SJoinAutoStopEvent(ServerInfo serverInfo, SwitcherServer switcherServer) {
        this.serverInfo = serverInfo;
        this.switcherServer = switcherServer;

    }

    public static SJoinAutoStopEvent callEvent(ServerInfo serverInfo, SwitcherServer switcherServer) {
        SJoinAutoStopEvent event = new SJoinAutoStopEvent(serverInfo, switcherServer);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
        return event;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public SwitcherServer getSwitcherServer() {
        return switcherServer;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
