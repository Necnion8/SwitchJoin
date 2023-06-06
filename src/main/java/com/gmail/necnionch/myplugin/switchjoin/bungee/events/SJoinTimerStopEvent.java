package com.gmail.necnionch.myplugin.switchjoin.bungee.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myplugin.switchjoin.bungee.timer.StopTimer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Event;

public class SJoinTimerStopEvent extends Event {
    private final ServerInfo serverInfo;
    private final SwitcherServer switcherServer;

    public SJoinTimerStopEvent(ServerInfo serverInfo, SwitcherServer switcherServer) {
        this.serverInfo = serverInfo;
        this.switcherServer = switcherServer;
    }

    public static SJoinTimerStopEvent callEvent(ServerInfo serverInfo, SwitcherServer switcherServer) {
        SJoinTimerStopEvent event = new SJoinTimerStopEvent(serverInfo, switcherServer);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
        return event;
    }

    public static SJoinTimerStopEvent callEvent(StopTimer timer) {
        SJoinTimerStopEvent event = new SJoinTimerStopEvent(timer.getBungeeServer(), timer.getSwitcherServer());
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
