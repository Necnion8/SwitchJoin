package com.gmail.necnionch.myplugin.switchjoin.bungee.hooks;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.utils.ServerState;
import com.gmail.necnionch.myplugin.switchjoin.bungee.SwitchJoin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.replacement.LiteralPlaceholder;
import net.minecrell.serverlistplus.core.replacement.ReplacementManager;

public class ServerListPlusPlaceholder extends LiteralPlaceholder {
    private final SwitchJoin plugin;

    protected ServerListPlusPlaceholder(SwitchJoin plugin) {
        super("%switchjoin_server_status%");
        this.plugin = plugin;

    }

    public static void register(SwitchJoin plugin) {
        ReplacementManager.getDynamic().add(new ServerListPlusPlaceholder(plugin));
    }

    private String getStatusName(ServerState state) {
        return ChatColor.translateAlternateColorCodes('&',
                SwitchJoin.getInstance().getMainConfig().getSlpStatusName(state.name().toUpperCase()));
    }

    @Override
    public String replace(ServerListPlusCore serverListPlusCore, String s) {
        if (!SwitchJoin.getInstance().isAvailable())
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
