package com.gmail.necnionch.myplugin.switchjoin.bungee.listeners;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.socket.data.ServerStartRequest;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.utils.ServerState;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.v1.CraftSwitcherAPI;
import com.gmail.necnionch.myplugin.switchjoin.bungee.MainConfig;
import com.gmail.necnionch.myplugin.switchjoin.bungee.SwitchJoin;
import com.gmail.necnionch.myplugin.switchjoin.bungee.events.SJoinAutoStartEvent;
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
    private final SwitchJoin main;
    private final MainConfig config;

    public PlayerEventListener(SwitchJoin main) {
        this.main = main;
        this.config = main.getMainConfig();

    }

    public static void register(SwitchJoin plugin) {
        plugin.getProxy().getPluginManager().registerListener(plugin, new PlayerEventListener(plugin));
    }


    @EventHandler
    public void onConnect(ServerConnectEvent event) {
        if (event.isCancelled() || !main.isAvailable())
            return;

        // check enabling
        if (!config.getIsAutoOpenJoin())
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
        if (main.getTemporaryServerBlacklist().contains(sServer.getId())) {
            main.getLogger().warning("Ignored because the last start failed.");

            String kickMessage = config.getFailToKickMessage();
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

        main.getLogger().info("Request to start " + sServer.getId() + " server");

        sServer.startFuture()
                .done((r) -> {
                    String fail = "";
                    if (r instanceof ServerStartRequest) {
                        ServerStartRequest res = (ServerStartRequest) r;
                        if (res.success)
                            return;
                        if (res.failMessage.equalsIgnoreCase("already running server"))  // TODO:
                            return;
                        fail = res.failMessage;
                    }
                    main.getLogger().warning(
                            "Invalid start response: " + fail
                    );
                    main.getTemporaryServerBlacklist().add(sServer.getId());
                })
                .fail((e) -> {
                    main.getLogger().warning(
                            "Invalid start response: " + e.getMessage()
                    );
                    main.getTemporaryServerBlacklist().add(sServer.getId());
                })
                .schedule();

        SJoinAutoStartEvent myEvent = SJoinAutoStartEvent.callEvent(event.getTarget(), sServer, event);
        if (myEvent.isCancelled())
            return;

        if (myEvent.getBroadcastMessage() != null)
            main.getProxy().broadcast(myEvent.getBroadcastMessage());

        event.setCancelled(true);
        BaseComponent[] text = TextComponent.fromLegacyText(
                ChatColor.GOLD + "サーバーを起動します" + getStartRemainingTimeMessage(sServer.getId()));
        if (event.getPlayer().getServer() != null) {
            event.getPlayer().sendMessage(text);
        } else {
            event.getPlayer().disconnect(text);
        }
    }

    @EventHandler
    public void onConnected(ServerConnectedEvent event) {
        if (!main.isAvailable())
            return;

        main.getTimerManager().stopTimer(event.getServer().getInfo());
    }

    @EventHandler
    public void onDisconnect(ServerDisconnectEvent event) {
        if (!main.isAvailable())
            return;

        if (!config.getIsAutoCloseEmpty())
            return;

        if (event.getTarget().getPlayers().isEmpty()) {
            main.getTimerManager().startTimer(event.getTarget());
        }
    }



    private Integer getStartRemainingTime(String serverId) {
        Integer lastStartTime = main.getMainConfig().getStartTime(serverId);
        if (lastStartTime == null)
            return null;

        Long starting = main.getStartingTimes().get(serverId);
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
