package com.gmail.necnionch.myplugin.switchjoin.bungee.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myplugin.switchjoin.bungee.SwitchJoin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class SJoinAutoStartEvent extends Event implements Cancellable {
    private boolean cancelled = false;
    private BaseComponent[] broadcastMessage;
    private final ServerConnectEvent connectEvent;
    private final ServerInfo serverInfo;
    private final SwitcherServer switcherServer;

    public SJoinAutoStartEvent(ServerInfo serverInfo, SwitcherServer switcherServer, ServerConnectEvent connectEvent) {
        this.serverInfo = serverInfo;
        this.switcherServer = switcherServer;
        this.connectEvent = connectEvent;

        broadcastMessage = TextComponent.fromLegacyText(
                ChatColor.YELLOW + SwitchJoin.getServerDisplayName(serverInfo.getName()) + "サーバーを起動します。");
    }

    public static SJoinAutoStartEvent callEvent(ServerInfo serverInfo, SwitcherServer switcherServer, ServerConnectEvent connectEvent) {
        SJoinAutoStartEvent event = new SJoinAutoStartEvent(serverInfo, switcherServer, connectEvent);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
        return event;
    }

    public BaseComponent[] getBroadcastMessage() {
        return broadcastMessage;
    }

    public void setBroadcastMessage(BaseComponent[] message) {
        this.broadcastMessage = message;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public SwitcherServer getSwitcherServer() {
        return switcherServer;
    }

    public ServerConnectEvent getConnectEvent() {
        return connectEvent;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
