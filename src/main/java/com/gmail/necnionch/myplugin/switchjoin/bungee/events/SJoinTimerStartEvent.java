package com.gmail.necnionch.myplugin.switchjoin.bungee.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Event;

public class SJoinTimerStartEvent extends Event {
    private final ServerInfo serverInfo;
    private final SwitcherServer switcherServer;

    public SJoinTimerStartEvent(ServerInfo serverInfo, SwitcherServer switcherServer) {
        this.serverInfo = serverInfo;
        this.switcherServer = switcherServer;
    }

    public static SJoinTimerStartEvent callEvent(ServerInfo serverInfo, SwitcherServer switcherServer) {
        SJoinTimerStartEvent event = new SJoinTimerStartEvent(serverInfo, switcherServer);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
        return event;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public SwitcherServer getSwitcherServer() {
        return switcherServer;
    }

}
