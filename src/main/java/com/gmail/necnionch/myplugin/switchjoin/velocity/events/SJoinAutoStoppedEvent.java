package com.gmail.necnionch.myplugin.switchjoin.velocity.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class SJoinAutoStoppedEvent extends BroadcastServerEvent {

    public SJoinAutoStoppedEvent(RegisteredServer server, SwitcherServer switcherServer, @Nullable Component broadcastMessage) {
        super(server, switcherServer, broadcastMessage);
    }

}
