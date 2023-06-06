package com.gmail.necnionch.myplugin.switchjoin.bungee;

import com.gmail.necnionch.myplugin.switchjoin.bungee.hooks.N8ServerUtilsHook;
import com.gmail.necnionch.myplugin.switchjoin.bungee.hooks.ServerListPlusPlaceholder;
import com.gmail.necnionch.myplugin.switchjoin.bungee.listeners.PlayerEventListener;
import com.gmail.necnionch.myplugin.switchjoin.bungee.listeners.ServerSendingListener;
import com.gmail.necnionch.myplugin.switchjoin.bungee.listeners.SwitcherEventListener;
import com.gmail.necnionch.myplugin.switchjoin.bungee.timer.StopTimerManager;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.*;

public final class SwitchJoin extends Plugin {
    private static SwitchJoin instance;
    private boolean available;
    private MainConfig mainConfig;
    private StopTimerManager timerManager;
    private final Set<String> temporaryServerBlacklist = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, Long> startingTimes = Collections.synchronizedMap(new HashMap<>());
    private static ServerNameReplacer serverNameReplacer = null;

    @Override
    public void onLoad() {
        if (getProxy().getPluginManager().getPlugin("ServerListPlus") != null) {
            ServerListPlusPlaceholder.register(this);
        }
    }

    @Override
    public void onEnable() {
        if (instance != null)
            throw new UnsupportedOperationException("already loaded");
        instance = this;

        try {
            Class.forName("com.gmail.necnionch.myapp.craftswitcherreportmodule.v1.CraftSwitcherAPI");
            available = true;

        } catch (ClassNotFoundException ignored) {
        }

        if (!available) {
            getLogger().severe("CraftSwitcherAPI class not found!");
            return;
        }

        mainConfig = new MainConfig(this);
        mainConfig.load();
        timerManager = new StopTimerManager(this);

        MainCommand.register(this);
        PlayerEventListener.register(this);
        SwitcherEventListener.register(this);


        Plugin tmp = getProxy().getPluginManager().getPlugin("DiscordConnect");
        if (tmp != null) {
            ServerSendingListener.register(this);
        }
        N8ServerUtilsHook n8su = new N8ServerUtilsHook();
        n8su.init();
        if (n8su.available()) {
            serverNameReplacer = n8su;
        }

    }

    @Override
    public void onDisable() {
        temporaryServerBlacklist.clear();
        mainConfig.save();

    }

    public static SwitchJoin getInstance() {
        return instance;
    }


    public boolean isAvailable() {
        return available;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public StopTimerManager getTimerManager() {
        return timerManager;
    }

    public Set<String> getTemporaryServerBlacklist() {
        return temporaryServerBlacklist;
    }

    public Map<String, Long> getStartingTimes() {
        return startingTimes;
    }

    public static void setServerNameReplacer(ServerNameReplacer replacer) {
        serverNameReplacer = replacer;
    }

    public static String getServerDisplayName(String server) {
        String replaced = null;
        if (serverNameReplacer != null) {
            replaced = serverNameReplacer.getServerDisplay(server);
        }
        return (replaced != null) ? replaced : server;
    }


}
