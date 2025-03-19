package com.gmail.necnionch.myplugin.switchjoin.velocity;

import com.gmail.necnionch.myplugin.switchjoin.common.timer.TimerManager;
import com.gmail.necnionch.myplugin.switchjoin.velocity.config.MainConfig;
import com.gmail.necnionch.myplugin.switchjoin.velocity.hooks.ServerListPlusPlaceholder;
import com.gmail.necnionch.myplugin.switchjoin.velocity.listeners.PlayerEventListener;
import com.gmail.necnionch.myplugin.switchjoin.velocity.listeners.SwitcherEventListener;
import com.gmail.necnionch.myplugin.switchjoin.velocity.platform.VelocityPlatform;
import com.gmail.necnionch.myplugin.switchjoin.velocity.platform.VelocityTimerManager;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "switchjoin",
        name = "SwitchJoin",
        version = BuildConstants.VERSION,
        authors = {"Necnion8"},
        dependencies = {
                @Dependency(id = "craftswitcherreportmodule"),
                @Dependency(id = "serverlistplus", optional = true),
        }
)
public class SwitchJoinPlugin {
    private final ProxyServer server;
    private final Logger logger;
    private final VelocityPlatform platform;
    private final VelocityTimerManager timerManager;
    private final MainConfig mainConfig;
    private boolean availableSwitcher;
    private @Nullable Object slpPlaceholder;

    @Inject
    public SwitchJoinPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataFolder) {
        this.server = server;
        this.logger = logger;
        this.platform = new VelocityPlatform(this, logger, server);
        this.mainConfig = new MainConfig(logger, dataFolder);
        this.timerManager = new VelocityTimerManager(platform, mainConfig);

        this.availableSwitcher = TimerManager.testSwitcherAPI();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        availableSwitcher = TimerManager.testSwitcherAPI();

        if (!availableSwitcher) {
            logger.error("Unavailable CraftSwitcher API");
            return;
        }

        mainConfig.load();

        if (server.getPluginManager().isLoaded("serverlistplus")) {
            slpPlaceholder = ServerListPlusPlaceholder.register(this, server);
        }

        server.getEventManager().register(this, new PlayerEventListener(platform, timerManager));
        server.getEventManager().register(this, new SwitcherEventListener(timerManager));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (!availableSwitcher)
            return;

        timerManager.clear();

        if (slpPlaceholder != null) {
            ServerListPlusPlaceholder.unregister((ServerListPlusPlaceholder) slpPlaceholder);
            slpPlaceholder = null;
        }
    }

    public MainConfig getConfig() {
        return mainConfig;
    }

    public boolean isAvailable() {
        return availableSwitcher;
    }

}

