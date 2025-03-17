package com.gmail.necnionch.myplugin.switchjoin.common.timer;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.socket.data.ServerStartRequest;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.socket.data.ServerStopRequest;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.utils.ServerState;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.v1.CraftSwitcherAPI;
import com.gmail.necnionch.myplugin.switchjoin.common.config.SwitchJoinConfig;
import com.gmail.necnionch.myplugin.switchjoin.common.events.EventResult;
import com.gmail.necnionch.myplugin.switchjoin.common.events.EventResultBroadcast;
import com.gmail.necnionch.myplugin.switchjoin.common.platform.Platform;
import com.gmail.necnionch.myplugin.switchjoin.common.platform.PlatformLogger;
import com.gmail.necnionch.myplugin.switchjoin.common.platform.PlatformServer;
import com.gmail.necnionch.myplugin.switchjoin.common.util.Message;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class TimerManager<P extends Platform<M, S>, M extends Message, S extends PlatformServer> implements StopTimer.Listener<S> {

    public static boolean testSwitcherAPI() {
        try {
            Class.forName("com.gmail.necnionch.myapp.craftswitcherreportmodule.v1.CraftSwitcherAPI");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    private final P platform;
    private final SwitchJoinConfig config;
    private final PlatformLogger log;
    private final Map<S, StopTimer<P, M, S>> serversTimer = new HashMap<>();
    private final Set<String> serverBlacklist = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, Long> startupTimes = Collections.synchronizedMap(new HashMap<>());

    public TimerManager(P platform, SwitchJoinConfig config) {
        this.platform = platform;
        this.config = config;
        this.log = platform.getLogger();
    }

    public P getPlatform() {
        return platform;
    }

    public SwitchJoinConfig getConfig() {
        return config;
    }

    public PlatformLogger getLogger() {
        return log;
    }

    public void startTimer(S server) {
        stopTimer(server);
        String targetName = config.getSwitcherServerId(server.getName());
        SwitcherServer sServer = null;
        if (targetName != null)
            sServer = CraftSwitcherAPI.getServer(targetName);
        if (sServer == null)
            return;

        StopTimer<P, M, S> timer = new StopTimer<>(platform, sServer, server, this);
        timer.setMinutes(config.getAutoCloseTimerMinutes(), config.getAutoCloseNotifyMinutes());
        timer.start();
        serversTimer.put(server, timer);

        callTimerStartEvent(server, sServer);
    }

    protected abstract void callTimerStartEvent(S platformServer, SwitcherServer switcherServer);

    public void stopTimer(S server) {
        StopTimer<P, M, S> timer = serversTimer.remove(server);

        if (timer != null && timer.stop()) {
            callTimerStopEvent(server, timer.getSwitcherServer());
        }
    }

    protected abstract void callTimerStopEvent(S platformServer, SwitcherServer switcherServer);

    public void stopTimer(StopTimer<P, M, S> timer) {
        for (StopTimer<P, M, S> t : new ArrayList<>(serversTimer.values()))
            if (t.equals(timer)) {
                if (serversTimer.remove(t.getPlatformServer()).stop())
                    callTimerStopEvent(t.getPlatformServer(), t.getSwitcherServer());
            }
    }

    public void stopTimer(SwitcherServer server) {
        for (StopTimer<P, M, S> timer : new ArrayList<>(serversTimer.values()))
            if (server.equals(timer.getSwitcherServer())) {
                serversTimer.remove(timer.getPlatformServer());
                if (timer.stop())
                    callTimerStopEvent(timer.getPlatformServer(), timer.getSwitcherServer());
            }
    }


    public void onPlayerQuit(S platformServer) {
        if (!config.isAutoCloseEmpty())
            return;

        if (platformServer.isEmpty()) {
            startTimer(platformServer);
        }
    }

    public void onPlayerJoin(S platformServer) {
        stopTimer(platformServer);
    }


    public StopTimer<P, M, S> getCurrentTimer(S server) {
        return serversTimer.get(server);
    }

    @SuppressWarnings("unchecked")
    public StopTimer<P, M, S>[] getTimers() {
        return serversTimer.values().toArray(new StopTimer[0]);
    }

    public Set<String> serverBlacklist() {
        return serverBlacklist;
    }

    public Map<String, Long> startupTimes() {
        return startupTimes;
    }

    public void clear() {
        new HashSet<>(serversTimer.values()).forEach(this::stopTimer);
        serverBlacklist.clear();
        serversTimer.clear();
    }


    @Override
    public void onTime(S s, SwitcherServer ss) {
        if (!(ServerState.STARTED.equals(ss.getState()) || ServerState.RUNNING.equals(ss.getState()))) {
            log.warn("Already not started/running state: " + ss.getId());
            return;
        }

        callAutoStopEvent(s, ss).thenAccept(result -> {
            if (result.isDenied())
                return;

            ss.stopFuture().done(r -> {
                if (r instanceof ServerStopRequest res) {
                    if (res.success) {
                        callAutoStoppedEvent(s, ss).thenAccept(platform::broadcast);
                    }
                }
            }).schedule();
        });
    }

    protected abstract CompletableFuture<EventResult> callAutoStopEvent(S platformServer, SwitcherServer switcherServer);

    protected abstract CompletableFuture<@Nullable M> callAutoStoppedEvent(S platformServer, SwitcherServer switcherServer);


    @Override
    public void onPreTime(S s, SwitcherServer ss, int remaining) {
        callPreAutoStopEvent(s, ss, remaining).thenAccept(platform::broadcast);
    }

    protected abstract CompletableFuture<@Nullable M> callPreAutoStopEvent(S platformServer, SwitcherServer switcherServer, int remaining);

    public CompletableFuture<EventResultBroadcast<M>> startServer(S platformServer, SwitcherServer switcherServer) {
        CompletableFuture<EventResultBroadcast<M>> result = callAutoStartEvent(platformServer, switcherServer).thenApply(r -> {
            if (r.result().isDenied())
                return r;

            platform.broadcast(r.message());
            return r;
        });

        platform.getLogger().info("Request to start " + switcherServer.getId() + " server");
        switcherServer.startFuture()
                .done(r -> {
                    String fail = "";
                    if (r instanceof ServerStartRequest res) {
                        if (res.success)
                            return;
                        if (res.failMessage.equalsIgnoreCase("already running server"))  // TODO:
                            return;
                        fail = res.failMessage;
                    }
                    platform.getLogger().warn("Invalid start response: " + fail);
                    serverBlacklist.add(switcherServer.getId());
                })
                .fail((e) -> {
                    platform.getLogger().warn("Invalid start response: " + e.getMessage());
                    serverBlacklist.add(switcherServer.getId());
                })
                .schedule();

        return result;
    }

    protected abstract CompletableFuture<EventResultBroadcast<M>> callAutoStartEvent(S platformServer, SwitcherServer switcherServer);


}
