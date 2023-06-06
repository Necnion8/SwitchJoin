package com.gmail.necnionch.myplugin.switchjoin.bungee.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myplugin.switchjoin.bungee.SwitchJoin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Event;

public class SJoinAutoStoppedEvent extends Event {
    private BaseComponent[] broadcastMessage;
    private final ServerInfo serverInfo;
    private final SwitcherServer switcherServer;

    public SJoinAutoStoppedEvent(ServerInfo serverInfo, SwitcherServer switcherServer) {
        this.serverInfo = serverInfo;
        this.switcherServer = switcherServer;

        broadcastMessage = TextComponent.fromLegacyText(
                ChatColor.YELLOW + SwitchJoin.getServerDisplayName(serverInfo.getName()) + "サーバーを停止しました。");
    }

    public static SJoinAutoStoppedEvent callEvent(ServerInfo serverInfo, SwitcherServer switcherServer) {
        SJoinAutoStoppedEvent event = new SJoinAutoStoppedEvent(serverInfo, switcherServer);
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

}
