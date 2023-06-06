package com.gmail.necnionch.myplugin.switchjoin.bungee.timer;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.socket.data.ServerStopRequest;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.utils.ServerState;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.v1.CraftSwitcherAPI;
import com.gmail.necnionch.myplugin.switchjoin.bungee.MainConfig;
import com.gmail.necnionch.myplugin.switchjoin.bungee.SwitchJoin;
import com.gmail.necnionch.myplugin.switchjoin.bungee.events.*;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StopTimerManager {
    private final SwitchJoin main;
    private final MainConfig config;
    private final Map<ServerInfo, StopTimer> serversTimer = new HashMap<>();

    public StopTimerManager(SwitchJoin plugin) {
        this.main = plugin;
        this.config = plugin.getMainConfig();

        StopTimer.setListener(this::onTime, this::onPreTime);
    }

    public void startTimer(ServerInfo server) {
        stopTimer(server);
        String targetName = config.getSwitcherServerId(server.getName());
        SwitcherServer sServer = null;
        if (targetName != null)
            sServer = CraftSwitcherAPI.getServer(targetName);
        if (sServer == null)
            return;

        StopTimer timer = new StopTimer(sServer, server);
        timer.setMinutes(config.getAutoCloseTimerMinutes(), config.getAutoCloseNotifyMinutes());
        timer.start();
        serversTimer.put(server, timer);

        SJoinTimerStartEvent.callEvent(server, sServer);
    }

    public void stopTimer(ServerInfo server) {
        StopTimer timer = serversTimer.remove(server);

        if (timer != null && timer.stop()) {
            SJoinTimerStopEvent.callEvent(server, timer.getSwitcherServer());
        }

    }

    public void stopTimer(StopTimer timer) {
        for (StopTimer t : new ArrayList<>(serversTimer.values()))
            if (t.equals(timer)) {
                if (serversTimer.remove(t.getBungeeServer()).stop())
                    SJoinTimerStopEvent.callEvent(t);
            }
    }

    public void stopTimer(SwitcherServer server) {
        for (StopTimer timer : new ArrayList<>(serversTimer.values()))
            if (server.equals(timer.getSwitcherServer())) {
                serversTimer.remove(timer.getBungeeServer());
                if (timer.stop()) {
                    SJoinTimerStopEvent.callEvent(timer);
                }
            }

    }

    public StopTimer getCurrentTimer(ServerInfo server) {
        return serversTimer.get(server);
    }

    public StopTimer[] getTimers() {
        return serversTimer.values().toArray(new StopTimer[0]);
    }


    private void onTime(ServerInfo s, SwitcherServer ss) {
        if (!(ServerState.STARTED.equals(ss.getState()) || ServerState.RUNNING.equals(ss.getState()))) {
            main.getLogger().warning("Already not started/running state: " + ss.getId());
            return;
        }

        if (SJoinAutoStopEvent.callEvent(s, ss).isCancelled())
            return;

        ss.stopFuture()
                .done(r -> {
                    if (r instanceof ServerStopRequest) {
                        ServerStopRequest res = (ServerStopRequest) r;
                        if (res.success) {
                            SJoinAutoStoppedEvent event = SJoinAutoStoppedEvent.callEvent(s, ss);
                            if (event.getBroadcastMessage() != null)
                                main.getProxy().broadcast(event.getBroadcastMessage());

                        }
                    }
                }).schedule();

    }

    private void onPreTime(ServerInfo s, SwitcherServer ss, int remaining) {
        SJoinPreAutoStopEvent event = SJoinPreAutoStopEvent.callEvent(s, ss, remaining);
        if (event.getBroadcastMessage() != null)
            main.getProxy().broadcast(event.getBroadcastMessage());

    }




}
