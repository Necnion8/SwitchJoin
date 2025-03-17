package com.gmail.necnionch.myplugin.switchjoin.bungee.listeners;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.utils.ServerState;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.v1.CraftSwitcherAPI;
import com.gmail.necnionch.myplugin.switchjoin.bungee.platform.BungeePlatform;
import com.gmail.necnionch.myplugin.switchjoin.bungee.platform.BungeeTimerManager;
import com.gmail.necnionch.myplugin.switchjoin.common.config.SwitchJoinConfig;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerEventListener implements Listener {

    private final BungeePlatform platform;
    private final BungeeTimerManager timerManager;
    private final SwitchJoinConfig config;

    public PlayerEventListener(BungeePlatform platform, BungeeTimerManager timerManager) {
        this.platform = platform;
        this.timerManager = timerManager;
        this.config = timerManager.getConfig();
    }

    @EventHandler
    public void onConnect(ServerConnectEvent event) {
        if (event.isCancelled())
            return;

        // check enabling
        if (!config.isAutoOpenJoin())
            return;
        if (!config.getAutoOpenJoinReasons().contains(event.getReason().name()))
            return;

        // online?
        if (!event.getTarget().getPlayers().isEmpty())
            return;

        if (event.getPlayer().getServer() == null) {  // join proxy
            // check fallback
            for (String sName : event.getPlayer().getPendingConnection().getListener().getServerPriority()) {
                ServerInfo sInfo = ProxyServer.getInstance().getServerInfo(sName);
                if (sInfo == null || sInfo.getName().equals(event.getTarget().getName())) {
                    continue;
                }
                if (!sInfo.getPlayers().isEmpty()) {
                    return;  // fallback server is online! (ignore start request)
                }
                String tName = config.getSwitcherServerId(sName);
                if (tName != null) {
                    SwitcherServer ss = CraftSwitcherAPI.getServer(tName);
                    if (ss != null && ServerState.STARTED.equals(ss.getState())) {
                        return;  // fallback server is online! (ignore start request)
                    }
                }
            }
        }

        // get SwitcherServer
        String targetName = config.getSwitcherServerId(event.getTarget().getName());
        if (targetName == null) {
            return;
        }
        SwitcherServer sServer = CraftSwitcherAPI.getServer(targetName);
        if (sServer == null) {
            return;
        }

        // check failed starting
        if (timerManager.serverBlacklist().contains(sServer.getId())) {
            platform.getLogger().warn("Ignored because the last start failed.");

            String kickMessage = config.getMessageFailKick();
            if (kickMessage != null && !kickMessage.isEmpty()) {
                event.setCancelled(true);

                BaseComponent[] message = TextComponent.fromLegacyText(
                        ChatColor.translateAlternateColorCodes('&', kickMessage));

                if (event.getPlayer().getServer() != null) {
                    event.getPlayer().sendMessage(message);
                } else {
                    event.getPlayer().disconnect(message);
                }
            }

            return;
        }

        switch (sServer.getState()) {
            case STOPPED:
                break;  // start

            case STARTING:
                // starting message
                Integer remaining = getStartRemainingTime(sServer.getId());
                if (remaining != null && remaining <= 3) {
                    event.getRequest().setRetry(true);
                    event.getRequest().setConnectTimeout(5 * 1000);
                } else {
                    event.setCancelled(true);
                    BaseComponent[] text = TextComponent.fromLegacyText(
                            ChatColor.GOLD + "サーバーを起動しています" + getStartRemainingTimeMessage(sServer.getId()));
                    if (event.getPlayer().getServer() != null) {
                        event.getPlayer().sendMessage(text);
                    } else {
                        event.getPlayer().disconnect(text);
                    }
                }
                return;

            case STOPPING:
                // processing message
                event.setCancelled(true);
                BaseComponent[] text = TextComponent.fromLegacyText(
                        ChatColor.GOLD + "しばらくお待ちください。サーバーが処理中です。");
                if (event.getPlayer().getServer() != null) {
                    event.getPlayer().sendMessage(text);
                } else {
                    event.getPlayer().disconnect(text);
                }
                return;

            default:
                return;  // started, unknown and other
        }

        timerManager.startServer(event.getTarget(), sServer).thenAccept(r -> {
            if (r.result().isDenied())
                return;

            event.setCancelled(true);
            BaseComponent[] text = TextComponent.fromLegacyText(
                    ChatColor.GOLD + "サーバーを起動します" + getStartRemainingTimeMessage(sServer.getId()));
            if (event.getPlayer().getServer() != null) {
                event.getPlayer().sendMessage(text);
            } else {
                event.getPlayer().disconnect(text);
            }
        });
    }

    @EventHandler
    public void onConnected(ServerConnectedEvent event) {
        timerManager.onPlayerJoin(event.getServer().getInfo());
    }

    @EventHandler
    public void onDisconnect(ServerDisconnectEvent event) {
        timerManager.onPlayerQuit(event.getTarget());
    }


    private Integer getStartRemainingTime(String serverId) {
        Integer lastStartTime = config.getStartTime(serverId);
        if (lastStartTime == null)
            return null;

        Long starting = timerManager.startupTimes().get(serverId);
        if (starting == null)
            return lastStartTime;

        int current = (int) ((System.currentTimeMillis() - starting) / 1000);
        return lastStartTime - current;

    }

    private String getStartRemainingTimeMessage(String serverId) {
        Integer remainingTime = getStartRemainingTime(serverId);

        if (remainingTime == null)
            return "。しばらくお待ちください。";

        int roundedTime = (int) (Math.round(remainingTime / 10d) * 10);
        if (roundedTime <= 0)
            return "";

        return ChatColor.WHITE + " / " + ChatColor.GRAY + "完了までおよそ" + roundedTime + "秒";
    }

}
