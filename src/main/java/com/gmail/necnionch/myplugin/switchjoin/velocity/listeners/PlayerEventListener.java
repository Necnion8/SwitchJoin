package com.gmail.necnionch.myplugin.switchjoin.velocity.listeners;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.utils.ServerState;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.v1.CraftSwitcherAPI;
import com.gmail.necnionch.myplugin.switchjoin.common.config.SwitchJoinConfig;
import com.gmail.necnionch.myplugin.switchjoin.velocity.platform.ConnectReason;
import com.gmail.necnionch.myplugin.switchjoin.velocity.platform.VelocityPlatform;
import com.gmail.necnionch.myplugin.switchjoin.velocity.platform.VelocityTimerManager;
import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.concurrent.CompletableFuture;

public class PlayerEventListener {

    private final VelocityPlatform platform;
    private final VelocityTimerManager timerManager;
    private final SwitchJoinConfig config;

    public PlayerEventListener(VelocityPlatform platform, VelocityTimerManager timerManager) {
        this.platform = platform;
        this.timerManager = timerManager;
        this.config = timerManager.getConfig();
    }

    @Subscribe
    public void onConnect(ServerPreConnectEvent event, Continuation continuation) {
        CompletableFuture<Void> future = processOnConnect(event);
        if (future != null) {
            future.whenComplete((v, ex) -> continuation.resume());
        } else {
            continuation.resume();
        }
    }

    private CompletableFuture<Void> processOnConnect(ServerPreConnectEvent event) {
        ServerPreConnectEvent.ServerResult result = event.getResult();
        if (!result.isAllowed())
            return null;

        RegisteredServer resultServer = result.getServer().orElse(null);
        if (resultServer == null)
            return null;

        // check enabling
        if (!config.isAutoOpenJoin())
            return null;

        ConnectReason reason = event.getPreviousServer() != null ? ConnectReason.SWITCH_SERVER : ConnectReason.JOIN_PROXY;
        if (!config.getAutoOpenJoinReasons().contains(reason.name()))
            return null;

        // logged?
        if (!resultServer.getPlayersConnected().isEmpty())
            return null;

        if (ConnectReason.JOIN_PROXY.equals(reason)) {
            // check fallback
            for (String sName : platform.getProxy().getConfiguration().getAttemptConnectionOrder()) {
                if (platform.getProxy().getServer(sName)
                        .map(s -> {
                            if (resultServer.equals(s))
                                return false;
                            if (!resultServer.getPlayersConnected().isEmpty())
                                return true;  // fallback server is online! (ignore start request)

                            String tName = config.getSwitcherServerId(sName);
                            if (tName != null) {
                                SwitcherServer ss = CraftSwitcherAPI.getServer(tName);
                                return ss != null && ServerState.STARTED.equals(ss.getState());  // fallback server is online! (ignore start request)
                            }
                            return false;
                        }).orElse(false)) {
                    System.out.println("6");
                    return null;
                }
            }
        }

        // get SwitcherServer
        String targetName = config.getSwitcherServerId(resultServer.getServerInfo().getName());
        if (targetName == null)
            return null;

        SwitcherServer sServer = CraftSwitcherAPI.getServer(targetName);
        if (sServer == null) {
            return null;
        }

        // check failed starting
        if (timerManager.serverBlacklist().contains(sServer.getId())) {
            platform.getLogger().warn("Ignored because the last start failed.");

            String kickMessage = config.getMessageFailKick();
            if (kickMessage != null && !kickMessage.isEmpty()) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());

                TextComponent message = LegacyComponentSerializer.builder().character('&').build().deserialize(kickMessage);
                if (ConnectReason.JOIN_PROXY.equals(reason)) {
                    event.getPlayer().disconnect(message);
                } else {
                    event.getPlayer().sendMessage(message);
                }
            }
            System.out.println("10");
            return null;
        }

        TextComponent text;
        switch (sServer.getState()) {
            case STOPPED:
                break;  // start

            case STARTING:
                // starting message
//                Integer remaining = getStartRemainingTime(sServer.getId());
//                if (remaining != null && remaining <= 3) {  // TODO: waiting join

                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                text = Component.text("サーバーを起動しています").color(NamedTextColor.GOLD).append(getStartRemainingTimeMessage(sServer.getId()));
                if (ConnectReason.JOIN_PROXY.equals(reason)) {
                    event.getPlayer().disconnect(text);
                } else {
                    event.getPlayer().sendMessage(text);
                }
                return null;

            case STOPPING:
                // processing message

                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                text = Component.text("しばらくお待ちください。サーバーが処理中です。").color(NamedTextColor.GOLD);
                if (ConnectReason.JOIN_PROXY.equals(reason)) {
                    event.getPlayer().disconnect(text);
                } else {
                    event.getPlayer().sendMessage(text);
                }
                return null;

            default:
                return null;  // started, unknown and other
        }

        return timerManager.startServer(resultServer, sServer).thenAccept(r -> {
            if (r.result().isDenied())
                return;

            event.setResult(ServerPreConnectEvent.ServerResult.denied());
            TextComponent text2 = Component.text("サーバーを起動します").color(NamedTextColor.GOLD).append(getStartRemainingTimeMessage(sServer.getId()));
            if (ConnectReason.JOIN_PROXY.equals(reason)) {
                event.getPlayer().disconnect(text2);
            } else {
                event.getPlayer().sendMessage(text2);
            }
        });
    }

    @Subscribe
    public void onConnected(ServerConnectedEvent event) {
        timerManager.onPlayerJoin(event.getServer());
        event.getPreviousServer().ifPresent(timerManager::onPlayerQuit);
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        event.getPlayer().getCurrentServer().map(ServerConnection::getServer).ifPresent(timerManager::onPlayerQuit);
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

    private Component getStartRemainingTimeMessage(String serverId) {
        Integer remainingTime = getStartRemainingTime(serverId);

        if (remainingTime == null)
            return Component.text("。しばらくお待ちください。");

        int roundedTime = (int) (Math.round(remainingTime / 10d) * 10);
        if (roundedTime <= 0)
            return Component.empty();

        return Component.text()
                .content(" / ").color(NamedTextColor.WHITE)
                .content("完了までおよそ" + roundedTime + "秒").color(NamedTextColor.GRAY).build();
    }

}
