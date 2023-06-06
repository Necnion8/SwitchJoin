package com.gmail.necnionch.myplugin.switchjoin.bungee.hooks;

import com.gmail.necnionch.myplugin.n8serverutils.bungee.N8ServerUtilsAPI;
import com.gmail.necnionch.myplugin.n8serverutils.bungee.N8ServerUtilsPlugin;
import com.gmail.necnionch.myplugin.switchjoin.bungee.ServerNameReplacer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class N8ServerUtilsHook implements ServerNameReplacer {
    private N8ServerUtilsAPI api;

    public N8ServerUtilsHook() {}

    public void init() {
        try {
            Plugin tmp = ProxyServer.getInstance().getPluginManager().getPlugin("N8ServerUtils");

            if (tmp == null)
                return;

            Class.forName("com.gmail.necnionch.myplugin.n8serverutils.bungee.N8ServerUtilsPlugin");

            this.api = ((N8ServerUtilsPlugin) tmp).getServerUtilsApi();

        } catch (ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
        }

    }

    public boolean available() {
        return api != null;
    }

    public String getServerDisplay(String server) {
        if (api != null)
            return api.getServerDisplay(server);
        return server;
    }
}
