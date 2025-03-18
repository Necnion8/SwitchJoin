package com.gmail.necnionch.myplugin.switchjoin.velocity.platform;

import com.gmail.necnionch.myplugin.switchjoin.common.platform.Platform;
import com.gmail.necnionch.myplugin.switchjoin.common.platform.PlatformLogger;
import com.gmail.necnionch.myplugin.switchjoin.velocity.SwitchJoinPlugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class VelocityPlatform implements Platform<VelocityMessage, VelocityServer> {
    private final SwitchJoinPlugin plugin;
    private final VelocityLogger logger;
    private final ProxyServer proxy;

    public VelocityPlatform(SwitchJoinPlugin plugin, Logger logger, ProxyServer proxy) {
        this.plugin = plugin;
        this.logger = new VelocityLogger(logger);
        this.proxy = proxy;
    }

    public SwitchJoinPlugin getPlugin() {
        return plugin;
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    @Override
    public PlatformLogger getLogger() {
        return logger;
    }

    @Override
    public void broadcast(@Nullable VelocityMessage message) {
        if (message != null) {
            proxy.getConsoleCommandSource().sendMessage(message.component());
            proxy.getAllPlayers().forEach(p -> p.sendMessage(message.component()));
        }
    }

    @Override
    public VelocityMessage formatLegacy(String message) {
        return new VelocityMessage(LegacyComponentSerializer
                .builder()
                .character('&')
                .build()
                .deserialize(message)
        );
    }

    @Override
    public VelocityTask runTaskLater(Runnable task, long delay, TimeUnit unit) {
        return new VelocityTask(getProxy().getScheduler().buildTask(plugin, task).delay(delay, unit).schedule());
    }

    @Override
    public Collection<VelocityServer> getServers() {
        return getProxy().getAllServers().stream().map(VelocityServer::of).collect(Collectors.toList());
    }


    @Override
    public String formatServerName(VelocityServer server) {
        return server.server().getServerInfo().getName();
    }

}
