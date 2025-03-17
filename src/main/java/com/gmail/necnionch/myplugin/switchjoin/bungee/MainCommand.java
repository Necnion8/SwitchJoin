package com.gmail.necnionch.myplugin.switchjoin.bungee;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.utils.ServerState;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.v1.CraftSwitcherAPI;
import com.gmail.necnionch.myplugin.switchjoin.bungee.config.MainConfig;
import com.gmail.necnionch.myplugin.switchjoin.bungee.platform.BungeeMessage;
import com.gmail.necnionch.myplugin.switchjoin.bungee.platform.BungeePlatform;
import com.gmail.necnionch.myplugin.switchjoin.bungee.platform.BungeeServer;
import com.gmail.necnionch.myplugin.switchjoin.bungee.platform.BungeeTimerManager;
import com.gmail.necnionch.myplugin.switchjoin.common.platform.PlatformServer;
import com.gmail.necnionch.myplugin.switchjoin.common.timer.StopTimer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainCommand extends Command implements TabExecutor {

    private final BungeeTimerManager timerManager;
    private final MainConfig config;
    private final BungeePlatform platform;

    public MainCommand(BungeeTimerManager timerManager, MainConfig config) {
        super("switchjoin", "switchjoin.command.switchjoin");
        this.timerManager = timerManager;
        this.config = config;
        this.platform = timerManager.getPlatform();
    }

    @Override
    public void execute(CommandSender s, String[] argsArrays) {
        /*
        /switchJoin config open
        /switchJoin config open toggle
        /switchJoin config open addReason (JOIN_PROXY...etc)
        /switchJoin config open removeReason (JOIN_PROXY...etc)
        /switchJoin config close
        /switchJoin config close toggle
        /switchJoin config close setTimerMinute (minutes)
        /switchJoin config close setNotifyTimerMinute (minutes)
        /switchJoin server
        /switchJoin server add (bungee:switcher)
        /switchJoin server remove (bungee:switcher)
        /switchJoin timer
        /switchJoin timer stop (bungee)
        /switchJoin timer start (bungee)
        /switchJoin reload
         */
        ArgumentParser args = new ArgumentParser(argsArrays);

        if (args.check("config", true)) {
            if (args.check("open", true)) {
                if (args.isEmpty()) {
                    // show current settings
                    if (config.isAutoOpenJoin()) {
                        info(s, "自動起動は&a有効&fになっています。");
                        info(s, "&7応答するイベント: &o" + (String.join("&7, &o", config.getAutoOpenJoinReasons())));
                    } else {
                        info(s, "自動起動は&c無効&fになっています。");
                    }

                } else if (args.check("toggle", true)) {
                    // toggle
                    boolean toggled = !config.isAutoOpenJoin();
                    config.setIsAutoOpenJoin(toggled);
                    info(s, "自動起動を" + ((toggled)?"&a有効":"&c無効") + "&fに設定しました。");
                    if (config.getAutoOpenJoinReasons().isEmpty()) {
                        info(s, "&e応答するイベントが設定されていません。");
                        info(s, "&7/switchjoin config open addreason &fで設定してください。");
                    }

                } else if (args.check("addReason", false)) {
                    // add reason
                    if (config.addAutoJoinReason(args.get(0))) {
                        info(s, "応答するイベントタイプを追加しました。");
                    } else {
                        error(s, "既に追加されています。");
                    }

                } else if (args.check("removeReason", false)) {
                    // remove reason
                    if (config.removeAutoJoinReason(args.get(0))) {
                        info(s, "応答するイベントタイプから除外しました。");
                    } else {
                        error(s, "既に除外されています。");
                    }

                } else {
                    // show help
                    info(s, "自動起動設定に関するコマンド:");
                    helpCommands(s, "/switchjoin config open", Arrays.asList(
                            new String[] {"toggle", "自動起動を有効または無効に切り替え"},
                            new String[] {"addreason (reason)", "応答するイベントの追加"},
                            new String[] {"removereason (reason)", "応答するイベントの除外"}
                    ));
                }
            } else if (args.check("close", true)) {
                if (args.isEmpty()) {
                    // show current settings
                    if (config.isAutoCloseEmpty()) {
                        info(s, "自動停止は&a有効&fになっています。");
                        if (config.getAutoCloseTimerMinutes() > config.getAutoCloseNotifyMinutes() && config.getAutoCloseNotifyMinutes() > 0) {
                            info(s, "&7タイマー時間: &f" + config.getAutoCloseTimerMinutes() + "分  &7(停止" + config.getAutoCloseNotifyMinutes() + "分前に通知)");
                        } else {
                            info(s, "&7タイマー時間: &f" + config.getAutoCloseTimerMinutes() + "分");
                        }
                    } else {
                        info(s, "自動起動は&c無効&fになっています。");
                    }

                } else if (args.check("toggle", true)) {
                    // toggle
                    boolean toggled = !config.isAutoCloseEmpty();
                    config.setIsAutoCloseEmpty(toggled);
                    info(s, "自動停止を" + ((toggled)?"&a有効":"&c無効") + "&fに設定しました。");
                    if (config.getAutoCloseTimerMinutes() <= 0) {
                        info(s, "&eタイマーが1分以上に設定されていません。");
                        info(s, "&7/switchjoin config close settimerminute &fで設定してください。");
                    }

                } else if (args.check("setTimerMinute", false)) {
                    // set
                    Integer minutes = args.getInteger(0);
                    if (minutes != null && minutes > 0) {
                        config.setAutoCloseTimerMinutes(minutes);
                        info(s, "自動停止タイマーを&e" + minutes + "&f分に設定しました。");

                    } else {
                        error(s, "1以上の数値を指定してください。");
                    }

                } else if (args.check("setNotifyTimerMinute", false)) {
                    // set
                    Integer minutes = args.getInteger(0);
                    if (minutes != null && minutes >= 0) {
                        config.setAutoCloseNotifyMinutes(minutes);
                        if (minutes == 0) {
                            info(s, "自動停止の通知を無効にしました。");
                        } else {
                            info(s, "自動停止 通知タイマーを&e" + minutes + "&f分に設定しました。");
                            if (config.getAutoCloseTimerMinutes() <= config.getAutoCloseNotifyMinutes()) {
                                info(s, "&e停止タイマーより短い時間のみ通知が有効になります。");
                            }
                        }
                    } else {
                        error(s, "0 &4(無効)&c または、1以上の数値を指定してください。");
                    }

                } else {
                    // show help
                    info(s, "自動停止設定に関するコマンド:");
                    helpCommands(s, "/switchjoin config close", Arrays.asList(
                            new String[] {"toggle", "自動停止を有効または無効に切り替え"},
                            new String[] {"settimerminute (minute)", "停止タイマー時間の設定 (分)"},
                            new String[] {"setnotifytimerminute (minute)", "停止通知タイマー時間の設定 (分)"}
                    ));
                }

            } else {
                // show help
                error(s, "/switchjoin config <open/close>");
            }

        } else if (args.check("server", true)) {
            if (args.isEmpty()) {
                // show servers
                info(s, "設定されているサーバー: &7(Bungee設定名 / CraftSwitcher設定名)");
                for (String bName : config.getPlatformServers()) {
                    String tName = config.getSwitcherServerId(bName);
                    s.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                            "&6- &b" + bName + " &7>> &2" + tName
                    )));
                }

            } else if (args.check("add", false)) {
                // add
                String[] split = args.get(0).split(":");
                if (split.length == 2) {
                    config.addServer(split[0], split[1]);
                    info(s, "サーバーを追加しました。&7(Bungee: " + split[0] + ", CraftSwitcher: " + split[1] + ")");
                } else {
                    error(s, "サーバーを &nBungee名:CraftSwitcher名&c で指定してください。");
                }

            } else if (args.check("remove", false)) {
                // remove
                config.removeServer(args.get(0));
                info(s, "サーバーを削除しました。");

            } else {
                // show help
                info(s, "サーバー設定に関するコマンド:");
                helpCommands(s, "/switchjoin server", Arrays.asList(
                        new String[] {"add (bungee:switcher)", "サーバーの追加"},
                        new String[] {"remove (bunee)", "サーバーの削除"}
                ));
            }
        } else if (args.check("timer", true)) {
            if (args.isEmpty()) {
                // show current
                info(s, "タイマー一覧:");
                for (StopTimer<?, ?, ?> timer : timerManager.getTimers()) {
                    if (!timer.isRunning())
                        continue;

                    s.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                            "&6- &2" + timer.getSwitcherServer().getId() + " &7>> &f" + formatTimerRemaining(timer)
                    )));
                }

            } else if (args.check("start", false)) {
                ServerInfo server = args.getServer(0);
                if (server != null) {
                    if (config.getSwitcherServerId(server.getName()) != null) {
                        StopTimer<?, ?, ?> timer = timerManager.getCurrentTimer(server);
                        boolean restart = (timer != null) && timer.isRunning();

                        timerManager.startTimer(server);
                        info(s, "タイマーを" + ((restart) ? "再" : "") + "起動しました。");
                    } else {
                        error(s, "そのサーバーは設定されていません。");
                    }
                } else {
                    error(s, "そのサーバーはありません。");
                }

            } else if (args.check("stop", false)) {
                ServerInfo server = args.getServer(0);
                if (server != null) {
                    StopTimer<BungeePlatform, BungeeMessage, BungeeServer> timer = timerManager.getCurrentTimer(server);
                    if (timer != null && timer.isRunning()) {
                        timerManager.stopTimer(timer);
                        info(s, "タイマーを停止しました。");

                    } else {
                        error(s, "タイマーは停止しています。");
                    }
                } else {
                    error(s, "そのサーバーはありません。");
                }

            } else {
                // show help
                info(s, "タイマー操作 コマンド:");
                helpCommands(s, "/switchjoin timer", Arrays.asList(
                        new String[] {"start (bungee)", "起動中のタイマーを停止"},
                        new String[] {"stop (bungee)", "停止中のタイマーを開始"}
                ));
            }
        } else if (args.check("reload", true)) {
            // reload configuration
            if (config.load()) {
                info(s, "設定ファイルを再読み込みしました。");
            } else {
                error(s, "エラーが発生しました。ログを参照してください。");
            }

        } else {
            // show help
            info(s, "SwitchJoin コマンド:");
            helpCommands(s, "/switchjoin", Arrays.asList(
                    new String[] {"config open", "自動開始設定"},
                    new String[] {"config close", "自動停止設定"},
                    new String[] {"server", "サーバー設定"},
                    new String[] {"timer", "タイマー操作"},
                    new String[] {"reload", "設定ファイルの再読み込み"}
            ));

        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] arguments) {
        ArgumentParser args = new ArgumentParser(arguments);
        if (args.isEmpty())
            return Collections.emptySet();

        if (args.check("config", false)) {
            if (args.check("open", false)) {
                if (args.check("addReason", false)) {
                    if (args.size() <= 1) {
                        return generateTabComplete(args,
                                Stream.of(ServerConnectEvent.Reason.values())
                                        .map(ServerConnectEvent.Reason::name)
                                        .filter(name -> !config.getAutoOpenJoinReasons().contains(name))
                                        .toArray(String[]::new)
                        );
                    }
                } else if (args.check("removeReason", false)) {
                    if (args.size() <= 1) {
                        return generateTabComplete(args,
                                Stream.of(ServerConnectEvent.Reason.values())
                                        .map(ServerConnectEvent.Reason::name)
                                        .filter(name -> config.getAutoOpenJoinReasons().contains(name))
                                        .toArray(String[]::new)
                        );
                    }
                } else if (args.size() <= 1) {
                    return generateTabComplete(args, "toggle", "addReason", "removeReason");
                }
            } else if (args.check("close", false)) {
                if (args.size() <= 1) {
                    return generateTabComplete(args, "toggle", "setTimerMinute", "setNotifyTimerMinute");
                }
            } else if (args.size() <= 1) {
                return generateTabComplete(args, "open", "close");
            }
        } else if (args.check("server", false)) {
            if (args.check("add", false)) {
                if (args.size() <= 1) {
                    List<String> list = new ArrayList<>();

                    platform.getServers().stream().map(BungeeServer::getName).forEach(name -> {
                        if (Arrays.asList(config.getPlatformServers()).contains(name))
                            return;
                        for (SwitcherServer sServer : CraftSwitcherAPI.getServers()) {
                            if (config.getPlatformServerId(sServer.getId()) == null) {
                                list.add(name + ":" + sServer.getId());
                            }
                        }
                    });
                    return generateTabComplete(args, list.toArray(new String[0]));
                }

            } else if (args.check("remove", false)) {
                if (args.size() <= 1) {
                    return generateTabComplete(args, config.getPlatformServers());
                }

            } else if (args.size() <= 1) {
                return generateTabComplete(args, "add", "remove");

            }

        } else if (args.check("timer", false)) {
            if (args.check("start", false)) {
                if (args.size() <= 1) {
                    return generateTabComplete(args, Stream.of(CraftSwitcherAPI.getServers())
                            .filter(s -> ServerState.STARTED.equals(s.getState()))
                            .map(s -> config.getPlatformServerId(s.getId()))
                            .filter(Objects::nonNull)
                            .toArray(String[]::new)
                    );
                }

            } else if (args.check("stop", false)) {
                if (args.size() <= 1) {
                    return generateTabComplete(args, Stream.of(timerManager.getTimers())
                            .filter(StopTimer::isRunning)
                            .map(StopTimer::getPlatformServer)
                            .map(PlatformServer::getName)
                            .toArray(String[]::new)
                    );
                }

            } else if (args.size() <= 1) {
                return generateTabComplete(args, "start", "stop");
            }

        } else if (args.size() <= 1) {
            return generateTabComplete(args, "config", "server", "timer", "reload");
        }

        return Collections.emptySet();
    }

    private void info(CommandSender s, String m) {
        s.sendMessage(TextComponent.fromLegacyText(
                ChatColor.WHITE + "[" + ChatColor.GOLD + "SwitchJoin" + ChatColor.WHITE + "] " +
                ChatColor.translateAlternateColorCodes('&', m)));
    }

    private void error(CommandSender s, String m) {
        s.sendMessage(TextComponent.fromLegacyText(
                ChatColor.WHITE + "[" + ChatColor.GOLD + "SwitchJoin" + ChatColor.WHITE + "] " + ChatColor.RED +
                ChatColor.translateAlternateColorCodes('&', m)));
    }

    private void helpCommands(CommandSender s, String b, List<String[]> commands) {
//        int maxLength = 0;
//        for (String[] e : commands) { maxLength = Math.max(maxLength, e[0].length()); }

        for (int index = 0; index < commands.size(); index++) {
            s.sendMessage(TextComponent.fromLegacyText(
                    ChatColor.WHITE + " ・ " + ChatColor.YELLOW + b + " " + commands.get(index)[0] + "\n" +
                            ChatColor.GRAY + ChatColor.ITALIC + "      " + commands.get(index)[1]
            ));
            if (index+1 < commands.size())
                s.sendMessage(TextComponent.fromLegacyText(String.valueOf(ChatColor.RESET)));
        }
    }

    private String formatTimerRemaining(StopTimer<?, ?, ?> timer) {
        int remaining = timer.getMinutes() * 60 - (int) ((System.currentTimeMillis() - timer.getTimerStartedTime()) / 1000);
        int p1 = remaining % 60;
        int p2 = remaining / 60;
        int p3 = p2 % 60;
        p2 = p2 / 60;
        if (p2 >= 1)
            return p2 + "h " + p3 + "m " + p1 + "s";
        if (p3 >= 1)
            return p3 + "m " + p1 + "s";
        return p1 + "s";
    }

    private List<String> generateTabComplete(List<String> args, String... list) {
        String input = (args.isEmpty()) ? "" : args.get(0);
        return Stream.of(list)
                .map(String::toLowerCase)
                .filter(e -> e.startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }



}
