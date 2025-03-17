package com.gmail.necnionch.myplugin.switchjoin.bungee.platform;

import com.gmail.necnionch.myplugin.switchjoin.common.platform.PlatformServer;
import net.md_5.bungee.api.config.ServerInfo;

public record BungeeServer(ServerInfo server) implements PlatformServer {

    @Override
    public String getName() {
        return server.getName();
    }

    @Override
    public boolean isEmpty() {
        return server.getPlayers().isEmpty();
    }

    public static BungeeServer of(ServerInfo serverInfo) {
        return new BungeeServer(serverInfo);
    }

}
