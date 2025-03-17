package com.gmail.necnionch.myplugin.switchjoin.bungee.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.Nullable;

public class SJoinAutoStoppedEvent extends BroadcastServerEvent {

    public SJoinAutoStoppedEvent(ServerInfo server, SwitcherServer switcherServer, @Nullable BaseComponent[] broadcastMessage) {
        super(server, switcherServer, broadcastMessage);
    }

}
