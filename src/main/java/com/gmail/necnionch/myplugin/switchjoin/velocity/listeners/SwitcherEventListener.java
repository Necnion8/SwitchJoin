package com.gmail.necnionch.myplugin.switchjoin.velocity.listeners;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.reporter.velocity.events.SwitcherServerRemoveEvent;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.reporter.velocity.events.SwitcherServerStateChangedEvent;
import com.gmail.necnionch.myplugin.switchjoin.common.config.SwitchJoinConfig;
import com.gmail.necnionch.myplugin.switchjoin.velocity.platform.VelocityTimerManager;
import com.velocitypowered.api.event.Subscribe;

import java.util.Optional;

public class SwitcherEventListener {
    private final VelocityTimerManager timerManager;
    private final SwitchJoinConfig config;

    public SwitcherEventListener(VelocityTimerManager timerManager) {
        this.timerManager = timerManager;
        this.config = timerManager.getConfig();
    }


    @Subscribe
    public void onChangeState(SwitcherServerStateChangedEvent event) {
        SwitcherServer sServer = event.getServer();

        switch (event.getState()) {
            case STARTED -> {
                timerManager.serverBlacklist().remove(sServer.getId());
                Long startTime = timerManager.startupTimes().remove(sServer.getId());
                if (startTime != null) {
                    config.putStartTime(sServer.getId(), (int) ((System.currentTimeMillis() - startTime) / 1000));
                }
                if (config.isAutoCloseEmpty()) {
                    Optional.ofNullable(config.getPlatformServerId(event.getServer().getId()))
                            .flatMap(name -> timerManager.getPlatform().getProxy().getServer(name))
                            .ifPresent(timerManager::startTimer);
                }
            }
            case RUNNING -> timerManager.startupTimes().put(sServer.getId(), System.currentTimeMillis());
            case STOPPED, STOPPING -> timerManager.stopTimer(event.getServer());
            default -> timerManager.startupTimes().remove(sServer.getId());
        }
    }

    @Subscribe
    public void onServerRemove(SwitcherServerRemoveEvent event) {
        SwitcherServer server = event.getServer();
        if (server != null) {
            timerManager.stopTimer(server);
        }
    }

}
