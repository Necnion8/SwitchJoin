package com.gmail.necnionch.myplugin.switchjoin.bungee.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Cancellable;
import org.jetbrains.annotations.Nullable;

public class SJoinAutoStartEvent extends BroadcastServerEvent implements Cancellable {
    private boolean cancelled;

    public SJoinAutoStartEvent(ServerInfo server, SwitcherServer switcherServer, @Nullable BaseComponent[] broadcastMessage) {
        super(server, switcherServer, broadcastMessage);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
