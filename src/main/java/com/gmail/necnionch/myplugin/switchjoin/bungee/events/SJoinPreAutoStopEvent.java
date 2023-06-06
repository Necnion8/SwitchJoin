package com.gmail.necnionch.myplugin.switchjoin.bungee.events;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myplugin.switchjoin.bungee.SwitchJoin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Event;

public class SJoinPreAutoStopEvent extends Event {
    private BaseComponent[] broadcastMessage;
    private final ServerInfo serverInfo;
    private final SwitcherServer switcherServer;
    private final Integer remainingMinutes;

    public SJoinPreAutoStopEvent(ServerInfo serverInfo, SwitcherServer switcherServer, Integer remainingMinutes) {
        this.serverInfo = serverInfo;
        this.switcherServer = switcherServer;
        this.remainingMinutes = remainingMinutes;

        if (remainingMinutes != null) {
            broadcastMessage = TextComponent.fromLegacyText(
                    ChatColor.GRAY + SwitchJoin.getServerDisplayName(serverInfo.getName()) + "サーバーが" + remainingMinutes + "分後に停止します。");
        } else {
            broadcastMessage = TextComponent.fromLegacyText(
                    ChatColor.GRAY + SwitchJoin.getServerDisplayName(serverInfo.getName()) + "サーバーがまもなく停止します。");
        }
    }

    public static SJoinPreAutoStopEvent callEvent(ServerInfo serverInfo, SwitcherServer switcherServer, int remainingMinutes) {
        SJoinPreAutoStopEvent event = new SJoinPreAutoStopEvent(serverInfo, switcherServer, remainingMinutes);
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

    public Integer getRemainingMinutes() {
        return remainingMinutes;
    }
}
