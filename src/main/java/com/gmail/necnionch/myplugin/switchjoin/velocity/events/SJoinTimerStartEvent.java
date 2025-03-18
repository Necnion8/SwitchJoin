package com.gmail.necnionch.myplugin.switchjoin.velocity.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

public class SJoinTimerStartEvent extends ServerEvent {

    public SJoinTimerStartEvent(RegisteredServer server, SwitcherServer switcherServer) {
        super(server, switcherServer);
    }

}
