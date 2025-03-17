package com.gmail.necnionch.myplugin.switchjoin.bungee.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import net.md_5.bungee.api.config.ServerInfo;

public class SJoinTimerStartEvent extends ServerEvent {

    public SJoinTimerStartEvent(ServerInfo server, SwitcherServer switcherServer) {
        super(server, switcherServer);
    }

}
