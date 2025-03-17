package com.gmail.necnionch.myplugin.switchjoin.bungee.platform;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myplugin.switchjoin.bungee.events.*;
import com.gmail.necnionch.myplugin.switchjoin.common.config.SwitchJoinConfig;
import com.gmail.necnionch.myplugin.switchjoin.common.events.EventResult;
import com.gmail.necnionch.myplugin.switchjoin.common.events.EventResultBroadcast;
import com.gmail.necnionch.myplugin.switchjoin.common.timer.StopTimer;
import com.gmail.necnionch.myplugin.switchjoin.common.timer.TimerManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Event;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BungeeTimerManager extends TimerManager<BungeePlatform, BungeeMessage, BungeeServer> {

    public BungeeTimerManager(BungeePlatform platform, SwitchJoinConfig config) {
        super(platform, config);
    }

    private <E extends Event> E callEvent(E event) {
        return getPlatform().getProxy().getPluginManager().callEvent(event);
    }

    public StopTimer<BungeePlatform, BungeeMessage, BungeeServer> getCurrentTimer(ServerInfo server) {
        return super.getCurrentTimer(BungeeServer.of(server));
    }

    public void startTimer(ServerInfo server) {
        super.startTimer(BungeeServer.of(server));
    }

    public void onPlayerQuit(ServerInfo server) {
        super.onPlayerQuit(BungeeServer.of(server));
    }

    public void onPlayerJoin(ServerInfo server) {
        super.onPlayerJoin(BungeeServer.of(server));
    }

    public CompletableFuture<EventResultBroadcast<BungeeMessage>> startServer(ServerInfo server, SwitcherServer switcherServer) {
        return super.startServer(BungeeServer.of(server), switcherServer);
    }


    @Override
    protected void callTimerStartEvent(BungeeServer platformServer, SwitcherServer switcherServer) {
        callEvent(new SJoinTimerStartEvent(platformServer.server(), switcherServer));
    }

    @Override
    protected void callTimerStopEvent(BungeeServer platformServer, SwitcherServer switcherServer) {
        callEvent(new SJoinTimerStopEvent(platformServer.server(), switcherServer));
    }

    @Override
    protected CompletableFuture<@Nullable BungeeMessage> callAutoStoppedEvent(BungeeServer platformServer, SwitcherServer switcherServer) {
        BaseComponent[] message = TextComponent.fromLegacyText("§e" + getPlatform().formatServerName(platformServer) + "サーバーを停止しました。");
        SJoinAutoStoppedEvent event = callEvent(new SJoinAutoStoppedEvent(platformServer.server(), switcherServer, message));
        return CompletableFuture.completedFuture(BungeeMessage.of(event.getBroadcastMessage()));
    }

    @Override
    protected CompletableFuture<@Nullable BungeeMessage> callPreAutoStopEvent(BungeeServer platformServer, SwitcherServer switcherServer, int remaining) {
        BaseComponent[] message = TextComponent.fromLegacyText("§7" + getPlatform().formatServerName(platformServer) + "サーバーが" + remaining + "分後に停止します。");
        SJoinPreAutoStopEvent event = callEvent(new SJoinPreAutoStopEvent(platformServer.server(), switcherServer, message, remaining));
        return CompletableFuture.completedFuture(BungeeMessage.of(event.getBroadcastMessage()));
    }

    @Override
    protected CompletableFuture<EventResult> callAutoStopEvent(BungeeServer platformServer, SwitcherServer switcherServer) {
        SJoinAutoStopEvent event = callEvent(new SJoinAutoStopEvent(platformServer.server(), switcherServer));
        return CompletableFuture.completedFuture(event.isCancelled() ? EventResult.DENY : EventResult.ALLOW);
    }

    @Override
    protected CompletableFuture<EventResultBroadcast<BungeeMessage>> callAutoStartEvent(BungeeServer platformServer, SwitcherServer switcherServer) {
        BaseComponent[] message = TextComponent.fromLegacyText("§e" + getPlatform().formatServerName(platformServer) + "サーバーを起動します。");
        SJoinAutoStartEvent event = callEvent(new SJoinAutoStartEvent(platformServer.server(), switcherServer, message));
        return CompletableFuture.completedFuture(new EventResultBroadcast<>(event.isCancelled() ? EventResult.DENY : EventResult.ALLOW, BungeeMessage.of(event.getBroadcastMessage())));
    }

}
