package com.gmail.necnionch.myplugin.switchjoin.bungee;

import com.gmail.necnionch.myplugin.switchjoin.bungee.config.MainConfig;
import com.gmail.necnionch.myplugin.switchjoin.bungee.hooks.N8ServerUtilsHook;
import com.gmail.necnionch.myplugin.switchjoin.bungee.hooks.ServerListPlusPlaceholder;
import com.gmail.necnionch.myplugin.switchjoin.bungee.listeners.PlayerEventListener;
import com.gmail.necnionch.myplugin.switchjoin.bungee.listeners.ServerSendingListener;
import com.gmail.necnionch.myplugin.switchjoin.bungee.listeners.SwitcherEventListener;
import com.gmail.necnionch.myplugin.switchjoin.bungee.platform.BungeePlatform;
import com.gmail.necnionch.myplugin.switchjoin.bungee.platform.BungeeServer;
import com.gmail.necnionch.myplugin.switchjoin.bungee.platform.BungeeTimerManager;
import com.gmail.necnionch.myplugin.switchjoin.common.timer.TimerManager;
import com.gmail.necnionch.myplugin.switchjoin.common.util.ServerNameResolver;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class SwitchJoinPlugin extends Plugin {
    private final BungeePlatform platform = new BungeePlatformImpl();
    private final MainConfig mainConfig = new MainConfig(this);
    private final BungeeTimerManager timerManager = new BungeeTimerManager(platform, mainConfig);
    private boolean availableSwitcher;
    private static ServerNameResolver serverNameResolver = null;
    private @Nullable Object slpPlaceholder;

    @Override
    public void onLoad() {
        availableSwitcher = TimerManager.testSwitcherAPI();

        if (getProxy().getPluginManager().getPlugin("ServerListPlus") != null) {
            slpPlaceholder = ServerListPlusPlaceholder.register(this);
        }
    }

    @Override
    public void onEnable() {
        if (!availableSwitcher) {
            getLogger().severe("Unavailable CraftSwitcher API");
            return;
        }

        mainConfig.load();

        getProxy().getPluginManager().registerCommand(this, new MainCommand(timerManager, mainConfig));
        getProxy().getPluginManager().registerListener(this, new PlayerEventListener(platform, timerManager));
        getProxy().getPluginManager().registerListener(this, new SwitcherEventListener(timerManager));

        Plugin tmp = getProxy().getPluginManager().getPlugin("DiscordConnect");
        if (tmp != null) {
            ServerSendingListener.register(this);
        }
        N8ServerUtilsHook n8su = new N8ServerUtilsHook();
        n8su.init();
        if (n8su.available()) {
            serverNameResolver = n8su;
        }

    }

    @Override
    public void onDisable() {
        if (!availableSwitcher)
            return;

        mainConfig.save();
        timerManager.clear();

        if (slpPlaceholder != null) {
            ServerListPlusPlaceholder.unregister((ServerListPlusPlaceholder) slpPlaceholder);
        }
    }


    public boolean isAvailable() {
        return availableSwitcher;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public BungeePlatform getPlatform() {
        return platform;
    }

    public BungeeTimerManager getTimerManager() {
        return timerManager;
    }


    public static void setServerNameReplacer(ServerNameResolver replacer) {
        serverNameResolver = replacer;
    }

    public static String getServerDisplayName(String server) {
        String replaced = null;
        if (serverNameResolver != null) {
            replaced = serverNameResolver.getServerDisplay(server);
        }
        return (replaced != null) ? replaced : server;
    }


    private class BungeePlatformImpl extends BungeePlatform {

        public BungeePlatformImpl() {
            super(SwitchJoinPlugin.this, SwitchJoinPlugin.this.getLogger());
        }

        @Override
        public String formatServerName(BungeeServer server) {
            return Optional.ofNullable(serverNameResolver)
                    .map(resolver -> resolver.getServerDisplay(server.getName()))
                    .orElse(server.getName());
        }
    }

}
