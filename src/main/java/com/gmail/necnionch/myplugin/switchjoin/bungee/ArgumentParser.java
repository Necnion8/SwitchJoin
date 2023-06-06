package com.gmail.necnionch.myplugin.switchjoin.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.Arrays;

public class ArgumentParser extends ArrayList<String> {
    public ArgumentParser(String[] args) {
        super(Arrays.asList(args));
    }

    public boolean check(String cmd, boolean emptyArg) {
        if (isEmpty())
            return false;
        if (!emptyArg && size() == 1)
            return false;
        if (get(0).equalsIgnoreCase(cmd)) {
            remove(0);
            return true;
        }
        return false;
    }

    public Integer getInteger(int index) {
        try {
            return Integer.parseInt(get(index));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public ServerInfo getServer(int index) {
        return ProxyServer.getInstance().getServerInfo(get(index));
    }


}
