package com.gmail.necnionch.myplugin.switchjoin.bungee.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import net.md_5.bungee.api.config.ServerInfo;

public abstract class ServerEvent extends SwitchJoinEvent {

    private final ServerInfo server;
    private final SwitcherServer switcherServer;

    public ServerEvent(ServerInfo server, SwitcherServer switcherServer) {
        this.server = server;
        this.switcherServer = switcherServer;
    }


    public ServerInfo getServer() {
        return server;
    }

    public SwitcherServer getSwitcherServer() {
        return switcherServer;
    }
}
