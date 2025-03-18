package com.gmail.necnionch.myplugin.switchjoin.velocity.platform;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myplugin.switchjoin.common.config.SwitchJoinConfig;
import com.gmail.necnionch.myplugin.switchjoin.common.events.EventResult;
import com.gmail.necnionch.myplugin.switchjoin.common.events.EventResultBroadcast;
import com.gmail.necnionch.myplugin.switchjoin.common.timer.StopTimer;
import com.gmail.necnionch.myplugin.switchjoin.common.timer.TimerManager;
import com.gmail.necnionch.myplugin.switchjoin.velocity.events.*;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class VelocityTimerManager extends TimerManager<VelocityPlatform, VelocityMessage, VelocityServer> {

    public VelocityTimerManager(VelocityPlatform platform, SwitchJoinConfig config) {
        super(platform, config);
    }

    private <E> CompletableFuture<E> fireEvent(E event) {
        return getPlatform().getProxy().getEventManager().fire(event);
    }

    public StopTimer<VelocityPlatform, VelocityMessage, VelocityServer> getCurrentTimer(RegisteredServer server) {
        return super.getCurrentTimer(VelocityServer.of(server));
    }

    public void startTimer(RegisteredServer server) {
        super.startTimer(VelocityServer.of(server));
    }

    public void onPlayerQuit(RegisteredServer server) {
        super.onPlayerQuit(VelocityServer.of(server));
    }

    public void onPlayerJoin(RegisteredServer server) {
        super.onPlayerJoin(VelocityServer.of(server));
    }

    public CompletableFuture<EventResultBroadcast<VelocityMessage>> startServer(RegisteredServer server, SwitcherServer switcherServer) {
        return super.startServer(VelocityServer.of(server), switcherServer);
    }


    @Override
    protected void callTimerStartEvent(VelocityServer platformServer, SwitcherServer switcherServer) {
        fireEvent(new SJoinTimerStartEvent(platformServer.server(), switcherServer));
    }

    @Override
    protected void callTimerStopEvent(VelocityServer platformServer, SwitcherServer switcherServer) {
        fireEvent(new SJoinTimerStopEvent(platformServer.server(), switcherServer));
    }

    @Override
    protected CompletableFuture<@Nullable VelocityMessage> callAutoStoppedEvent(VelocityServer platformServer, SwitcherServer switcherServer) {
        TextComponent message = Component.text(getPlatform().formatServerName(platformServer) + "サーバーを停止しました。").color(NamedTextColor.YELLOW);
        return fireEvent(new SJoinAutoStoppedEvent(platformServer.server(), switcherServer, message))
                .thenApply(event -> VelocityMessage.of(event.getBroadcastMessage()));
    }

    @Override
    protected CompletableFuture<@Nullable VelocityMessage> callPreAutoStopEvent(VelocityServer platformServer, SwitcherServer switcherServer, int remaining) {
        TextComponent message = Component.text(getPlatform().formatServerName(platformServer) + "サーバーが" + remaining + "分後に停止します。").color(NamedTextColor.GRAY);
        return fireEvent(new SJoinPreAutoStopEvent(platformServer.server(), switcherServer, message, remaining))
                .thenApply(event -> VelocityMessage.of(event.getBroadcastMessage()));
    }

    @Override
    protected CompletableFuture<EventResult> callAutoStopEvent(VelocityServer platformServer, SwitcherServer switcherServer) {
        return fireEvent(new SJoinAutoStopEvent(platformServer.server(), switcherServer))
                .thenApply(event -> event.isCancelled() ? EventResult.DENY : EventResult.ALLOW);
    }

    @Override
    protected CompletableFuture<EventResultBroadcast<VelocityMessage>> callAutoStartEvent(VelocityServer platformServer, SwitcherServer switcherServer) {
        TextComponent message = Component.text(getPlatform().formatServerName(platformServer) + "サーバーを起動します。").color(NamedTextColor.YELLOW);
        return fireEvent(new SJoinAutoStartEvent(platformServer.server(), switcherServer, message))
                .thenApply(event -> new EventResultBroadcast<>(event.isCancelled() ? EventResult.DENY : EventResult.ALLOW, VelocityMessage.of(event.getBroadcastMessage())));
    }

}
