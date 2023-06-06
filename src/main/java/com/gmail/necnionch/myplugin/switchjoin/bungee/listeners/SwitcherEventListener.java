package com.gmail.necnionch.myplugin.switchjoin.bungee.listeners;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.reporter.bungee.events.SwitcherServerRemoveEvent;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.reporter.bungee.events.SwitcherServerStateChangedEvent;
import com.gmail.necnionch.myplugin.switchjoin.bungee.SwitchJoin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class SwitcherEventListener implements Listener {
    private final SwitchJoin main;


    public SwitcherEventListener(SwitchJoin main) {
        this.main = main;
    }

    public static void register(SwitchJoin plugin) {
        plugin.getProxy().getPluginManager().registerListener(plugin, new SwitcherEventListener(plugin));
    }


    @EventHandler
    public void onChangeState(SwitcherServerStateChangedEvent event) {
        SwitcherServer sServer = event.getServer();

        switch (event.getState()) {
            case STARTED:
                main.getTemporaryServerBlacklist().remove(sServer.getId());

                Long startTime = main.getStartingTimes().remove(sServer.getId());
                if (startTime != null) {
                    main.getMainConfig().putStartTime(
                            sServer.getId(), (int) ((System.currentTimeMillis() - startTime) / 1000)
                    );
                }

                if (main.getMainConfig().getIsAutoCloseEmpty()) {
                    String bungeeName = main.getMainConfig().getBungeeServerId(event.getServer().getId());
                    ServerInfo sInfo = ProxyServer.getInstance().getServerInfo(bungeeName);
                    if (sInfo != null) {
                        main.getTimerManager().startTimer(sInfo);
                    }
                }
                break;

            case RUNNING:
                main.getStartingTimes().put(sServer.getId(), System.currentTimeMillis());
                break;

            case STOPPED:
            case STOPPING:
                main.getTimerManager().stopTimer(event.getServer());
                break;

            default:
                main.getStartingTimes().remove(sServer.getId());

        }
    }

    @EventHandler
    public void onServerRemove(SwitcherServerRemoveEvent event) {
        SwitcherServer server = event.getServer();
        if (server != null) {
            main.getTimerManager().stopTimer(server);
        }
    }

}
