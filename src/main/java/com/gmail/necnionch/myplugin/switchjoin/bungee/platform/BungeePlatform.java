package com.gmail.necnionch.myplugin.switchjoin.bungee.platform;

import com.gmail.necnionch.myplugin.switchjoin.bungee.SwitchJoinPlugin;
import com.gmail.necnionch.myplugin.switchjoin.common.platform.Platform;
import com.gmail.necnionch.myplugin.switchjoin.common.platform.PlatformLogger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class BungeePlatform implements Platform<BungeeMessage, BungeeServer> {
    private final BungeeLogger logger;
    private final SwitchJoinPlugin plugin;

    public BungeePlatform(SwitchJoinPlugin plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = new BungeeLogger(logger);
    }

    public SwitchJoinPlugin getPlugin() {
        return plugin;
    }

    public ProxyServer getProxy() {
        return plugin.getProxy();
    }

    @Override
    public PlatformLogger getLogger() {
        return logger;
    }

    @Override
    public void broadcast(@Nullable BungeeMessage message) {
        if (message != null)
            getProxy().broadcast(message.components());
    }

    @Override
    public BungeeMessage formatLegacy(String message) {
        return new BungeeMessage(TextComponent.fromLegacyText(message));
    }

    @Override
    public BungeeTask runTaskLater(Runnable task, long delay, TimeUnit unit) {
        return new BungeeTask(getProxy().getScheduler().schedule(plugin, task, delay, unit));
    }

    @Override
    public Collection<BungeeServer> getServers() {
        return getProxy().getServers().values().stream().map(BungeeServer::of).collect(Collectors.toList());
    }

}
