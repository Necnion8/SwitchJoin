package com.gmail.necnionch.myplugin.switchjoin.velocity.platform;

import com.gmail.necnionch.myplugin.switchjoin.common.platform.PlatformServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

public record VelocityServer(RegisteredServer server) implements PlatformServer {

    @Override
    public String getName() {
        return server.getServerInfo().getName();
    }

    @Override
    public boolean isEmpty() {
        return server.getPlayersConnected().isEmpty();
    }

    public static VelocityServer of(RegisteredServer server) {
        return new VelocityServer(server);
    }

}
