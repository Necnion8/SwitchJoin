package com.gmail.necnionch.myplugin.switchjoin.bungee.listeners;

import com.github.nova_27.mcplugin.discordconnect.DiscordConnect;
import com.github.nova_27.mcplugin.discordconnect.utils.Messages;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.reporter.bungee.events.SwitcherServerStateChangedEvent;
import com.gmail.necnionch.myplugin.switchjoin.bungee.SwitchJoinPlugin;
import com.gmail.necnionch.myplugin.switchjoin.bungee.events.SJoinPreAutoStopEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.awt.*;

public class ServerSendingListener implements Listener {
    private final SwitchJoinPlugin main;

    public ServerSendingListener(SwitchJoinPlugin main) {
        this.main = main;
    }

    public static void register(SwitchJoinPlugin plugin) {
        plugin.getProxy().getPluginManager().registerListener(plugin, new ServerSendingListener(plugin));
    }



    @EventHandler
    public void onServerStateChanged(SwitcherServerStateChangedEvent event) {
        String bungeeName = main.getMainConfig().getPlatformServerId(event.getServer().getId());
        if (bungeeName == null)
            return;

        String m;
        Color c;

        switch (event.getState()) {
            case STARTING -> {
                m = Messages.ServerStarting.toString();
                c = Color.YELLOW;
            }
            case STARTED -> {
                m = Messages.ServerStarted.toString();
                c = Color.GREEN;
            }
            case STOPPING -> {
                m = Messages.ServerStopping.toString();
                c = Color.YELLOW;
            }
            case STOPPED -> {
                m = Messages.ServerStopped.toString();
                c = Color.RED;
            }
            default -> {
                return;
            }
        }

        m = m.replace("{server}", SwitchJoinPlugin.getServerDisplayName(bungeeName));
        DiscordConnect.getInstance().embed(c, m, null);
    }

    @EventHandler
    public void onTimerStarted(SJoinPreAutoStopEvent event) {
        String bungeeName = main.getMainConfig().getPlatformServerId(event.getSwitcherServer().getId());
        if (bungeeName == null)
            return;

        DiscordConnect.getInstance().embed(Color.ORANGE, Messages.TimerStarted.toString()
                .replace("{time}", event.getRemainingMinutes().toString())
                .replace("{server}", SwitchJoinPlugin.getServerDisplayName(bungeeName)), null);

    }


}

