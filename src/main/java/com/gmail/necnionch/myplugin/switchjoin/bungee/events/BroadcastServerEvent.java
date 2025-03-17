package com.gmail.necnionch.myplugin.switchjoin.bungee.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.Nullable;

public abstract class BroadcastServerEvent extends ServerEvent implements Broadcast {

    private @Nullable BaseComponent[] broadcastMessage;

    public BroadcastServerEvent(ServerInfo server, SwitcherServer switcherServer, @Nullable BaseComponent[] broadcastMessage) {
        super(server, switcherServer);
        this.broadcastMessage = broadcastMessage;
    }

    @Override
    @Nullable
    public BaseComponent[] getBroadcastMessage() {
        return broadcastMessage;
    }

    @Override
    public void setBroadcastMessage(@Nullable BaseComponent[] message) {
        this.broadcastMessage = message;
    }

}
