package com.gmail.necnionch.myplugin.switchjoin.velocity.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;


public abstract class BroadcastServerEvent extends ServerEvent implements Broadcast {

    private @Nullable Component broadcastMessage;

    public BroadcastServerEvent(RegisteredServer server, SwitcherServer switcherServer, @Nullable Component broadcastMessage) {
        super(server, switcherServer);
        this.broadcastMessage = broadcastMessage;
    }

    @Override
    @Nullable
    public Component getBroadcastMessage() {
        return broadcastMessage;
    }

    @Override
    public void setBroadcastMessage(@Nullable Component message) {
        this.broadcastMessage = message;
    }

}
