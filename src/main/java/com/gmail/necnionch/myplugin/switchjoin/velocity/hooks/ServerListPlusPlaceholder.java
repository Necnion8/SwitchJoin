package com.gmail.necnionch.myplugin.switchjoin.velocity.hooks;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.utils.ServerState;
import com.gmail.necnionch.myplugin.switchjoin.velocity.SwitchJoinPlugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.replacement.LiteralPlaceholder;
import net.minecrell.serverlistplus.core.replacement.ReplacementManager;

public class ServerListPlusPlaceholder extends LiteralPlaceholder {
    private final SwitchJoinPlugin plugin;
    private final ProxyServer server;

    public ServerListPlusPlaceholder(SwitchJoinPlugin plugin, ProxyServer server) {
        super("%switchjoin_server_status%");
        this.plugin = plugin;
        this.server = server;

    }

    public static ServerListPlusPlaceholder register(SwitchJoinPlugin plugin, ProxyServer server) {
        ServerListPlusPlaceholder placeholder = new ServerListPlusPlaceholder(plugin, server);
        ReplacementManager.getDynamic().add(placeholder);
        return placeholder;
    }

    public static void unregister(ServerListPlusPlaceholder placeholder) {
        ReplacementManager.getDynamic().remove(placeholder);
    }

    private String getStatusName(ServerState state) {
        return plugin.getConfig().getSlpStatusName(state.name().toUpperCase());
    }

    @Override
    public String replace(ServerListPlusCore serverListPlusCore, String s) {
        if (!plugin.isAvailable())
            return "";

        for (String serverName : server.getConfiguration().getAttemptConnectionOrder()) {
            RegisteredServer server = this.server.getServer(serverName).orElse(null);
            if (server == null)
                continue;

            String swiName = plugin.getConfig().getSwitcherServerId(serverName);
            SwitcherServer swiServer = SwitcherServer.getServer(swiName);
            if (swiServer != null)
                return this.replace(s, getStatusName(swiServer.getState()));
        }

        return this.replace(s, getStatusName(ServerState.UNKNOWN));
    }

}
