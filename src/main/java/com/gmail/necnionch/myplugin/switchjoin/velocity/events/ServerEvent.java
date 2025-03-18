package com.gmail.necnionch.myplugin.switchjoin.velocity.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

public abstract class ServerEvent extends SwitchJoinEvent {

    private final RegisteredServer server;
    private final SwitcherServer switcherServer;

    public ServerEvent(RegisteredServer server, SwitcherServer switcherServer) {
        this.server = server;
        this.switcherServer = switcherServer;
    }


    public RegisteredServer getServer() {
        return server;
    }

    public SwitcherServer getSwitcherServer() {
        return switcherServer;
    }
}
