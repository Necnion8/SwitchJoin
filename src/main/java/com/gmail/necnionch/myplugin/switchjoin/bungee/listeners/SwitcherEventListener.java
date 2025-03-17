package com.gmail.necnionch.myplugin.switchjoin.bungee.listeners;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.reporter.bungee.events.SwitcherServerRemoveEvent;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.reporter.bungee.events.SwitcherServerStateChangedEvent;
import com.gmail.necnionch.myplugin.switchjoin.bungee.platform.BungeeTimerManager;
import com.gmail.necnionch.myplugin.switchjoin.common.config.SwitchJoinConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class SwitcherEventListener implements Listener {
    private final BungeeTimerManager timerManager;
    private final SwitchJoinConfig config;

    public SwitcherEventListener(BungeeTimerManager timerManager) {
        this.timerManager = timerManager;
        this.config = timerManager.getConfig();
    }


    @EventHandler
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
                    String bungeeName = config.getPlatformServerId(event.getServer().getId());
                    ServerInfo sInfo = ProxyServer.getInstance().getServerInfo(bungeeName);
                    if (sInfo != null) {
                        timerManager.startTimer(sInfo);
                    }
                }
            }
            case RUNNING -> timerManager.startupTimes().put(sServer.getId(), System.currentTimeMillis());
            case STOPPED, STOPPING -> timerManager.stopTimer(event.getServer());
            default -> timerManager.startupTimes().remove(sServer.getId());
        }
    }

    @EventHandler
    public void onServerRemove(SwitcherServerRemoveEvent event) {
        SwitcherServer server = event.getServer();
        if (server != null) {
            timerManager.stopTimer(server);
        }
    }

}
