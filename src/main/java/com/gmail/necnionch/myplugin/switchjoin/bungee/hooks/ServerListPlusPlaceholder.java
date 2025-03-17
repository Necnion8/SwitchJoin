package com.gmail.necnionch.myplugin.switchjoin.bungee.hooks;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.utils.ServerState;
import com.gmail.necnionch.myplugin.switchjoin.bungee.SwitchJoinPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.replacement.LiteralPlaceholder;
import net.minecrell.serverlistplus.core.replacement.ReplacementManager;

public class ServerListPlusPlaceholder extends LiteralPlaceholder {
    private final SwitchJoinPlugin plugin;

    public ServerListPlusPlaceholder(SwitchJoinPlugin plugin) {
        super("%switchjoin_server_status%");
        this.plugin = plugin;

    }

    public static ServerListPlusPlaceholder register(SwitchJoinPlugin plugin) {
        ServerListPlusPlaceholder placeholder = new ServerListPlusPlaceholder(plugin);
        ReplacementManager.getDynamic().add(placeholder);
        return placeholder;
    }

    public static void unregister(ServerListPlusPlaceholder placeholder) {
        ReplacementManager.getDynamic().remove(placeholder);
    }

    private String getStatusName(ServerState state) {
        return ChatColor.translateAlternateColorCodes('&',
                plugin.getMainConfig().getSlpStatusName(state.name().toUpperCase()));
    }

    @Override
    public String replace(ServerListPlusCore serverListPlusCore, String s) {
        if (!plugin.isAvailable())
            return "";

        ListenerInfo[] listeners = ProxyServer.getInstance().getConfig().getListeners().toArray(new ListenerInfo[0]);
        if (listeners.length >= 1) {
            ServerInfo sInfo;
            SwitcherServer sServer;
            for (String name : listeners[0].getServerPriority()) {
                sInfo = ProxyServer.getInstance().getServerInfo(name);
                if (sInfo == null)
                    continue;

                name = plugin.getMainConfig().getSwitcherServerId(name);
                sServer = SwitcherServer.getServer(name);

                if (sServer != null) {
                    return this.replace(s, getStatusName(sServer.getState()));
                }
            }
        }
        return this.replace(s, getStatusName(ServerState.UNKNOWN));
    }

}
