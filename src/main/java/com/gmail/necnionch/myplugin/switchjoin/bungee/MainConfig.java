package com.gmail.necnionch.myplugin.switchjoin.bungee;

import com.gmail.necnionch.myplugin.switchjoin.common.BungeeConfigDriver;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MainConfig extends BungeeConfigDriver {
    private List<String> autoOpenJoinReasons = Collections.emptyList();
    private final Map<String, List<Integer>> lastStartTimes = Collections.synchronizedMap(new HashMap<>());

    public MainConfig(Plugin plugin) {
        super(plugin);
    }


    public boolean getIsAutoOpenJoin() {
        return config.getBoolean("auto-open-join.enable", false);
    }

    public List<String> getAutoOpenJoinReasons() {
        return autoOpenJoinReasons;
    }

    public String getFailToKickMessage() {
        return config.getString("auto-open-join.fail-to-kick-message");
    }

    public boolean getIsAutoCloseEmpty() {
        return getAutoCloseEmptySection().getBoolean("enable", false);
    }

    public int getAutoCloseTimerMinutes() {
        return getAutoCloseEmptySection().getInt("timer-minutes", 120);
    }

    public int getAutoCloseNotifyMinutes() {
        return getAutoCloseEmptySection().getInt("notify-minutes", 5);
    }

    public String getSwitcherServerId(String serverName) {
        return config.getString("servers." + serverName, null);
    }

    public String getBungeeServerId(String switcherServerName) {
        for (String bungeeName : config.getSection("servers").getKeys()) {
            String targetName = config.getString("servers." + bungeeName);
            if (switcherServerName.equals(targetName))
                return bungeeName;
        }
        return null;
    }

    public void putStartTime(String serverId, int startTime) {
        List<Integer> times = lastStartTimes.get(serverId);
        if (times == null)
            times = new ArrayList<>();

        times.add(0, startTime);
        while (times.size() > 10) {
            times.remove(10);
        }
        lastStartTimes.put(serverId, times);
    }

    public Integer getStartTime(String serverId) {
        if (lastStartTimes.containsKey(serverId)) {
            List<Integer> times = lastStartTimes.get(serverId);
            if (times.isEmpty())
                return null;
            int sum = times.stream().mapToInt(i -> i).sum();
            return sum / times.size();
        }
        return null;
    }

    public String getSlpStatusName(String key) {
        return config.getString("serverlistplus.status." + key.toUpperCase(), key);
    }

    public String[] getSlpStatusNameKeys() {
        return config.getSection("serverlistplus.status").getKeys().stream()
                .map(String::toUpperCase)
                .toArray(String[]::new);
    }

    // setter

    public void setIsAutoOpenJoin(boolean enable) {
        config.set("auto-open-join.enable", enable);
        save();
    }

    public boolean addAutoJoinReason(String reason) {
        Set<String> set = new HashSet<>(autoOpenJoinReasons);
        boolean result = set.add(reason.toUpperCase());
        autoOpenJoinReasons.clear();
        autoOpenJoinReasons.addAll(set);
        save();
        return result;
    }

    public boolean removeAutoJoinReason(String reason) {
        Set<String> set = new HashSet<>(autoOpenJoinReasons);
        boolean result = set.remove(reason.toUpperCase());
        autoOpenJoinReasons.clear();
        autoOpenJoinReasons.addAll(set);
        save();
        return result;
    }

    public void setIsAutoCloseEmpty(boolean enable) {
        getAutoCloseEmptySection().set("enable", enable);
        save();
    }

    public void setAutoCloseTimerMinutes(int minutes) {
        getAutoCloseEmptySection().set("timer-minutes", minutes);
        save();
    }

    public void setAutoCloseNotifyMinutes(int minutes) {
        getAutoCloseEmptySection().set("notify-minutes", minutes);
        save();
    }

    public void addServer(String bungeeName, String switcherName) {
        Map<String, String> map = new HashMap<>();
        for (String bName : config.getSection("servers").getKeys()) {
            map.put(bName, config.getString("servers." + bName));
        }
        map.put(bungeeName, switcherName);
        config.set("servers", map);
        save();
    }

    public void removeServer(String bungeeName) {
        config.set("servers." + bungeeName, null);
        save();
    }

    public void setSlpStatusName(String key, String value) {
        config.set("serverlistplus.status." + key.toUpperCase(), value);
    }


    private Configuration getAutoCloseEmptySection() {
        return config.getSection("auto-close-empty");
    }

    public String[] getBungeeServers() {
        return config.getSection("servers").getKeys().toArray(new String[0]);
    }



    @Override
    public boolean onLoaded(Configuration config) {
        List<String> list = config.getStringList("auto-open-join.reason");
        autoOpenJoinReasons = list.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        lastStartTimes.clear();
        for (String serverId : config.getSection("last-start-time").getKeys()) {
            String flat = config.getString("last-start-time." + serverId);
            if (flat != null) {
                lastStartTimes.put(serverId, Stream.of(flat.split(","))
                        .map(s -> {
                            try {
                                return Integer.parseInt(s);
                            } catch (NumberFormatException e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
                );
            }
        }

        return true;
    }

    @Override
    public boolean save() {
        config.set("auto-open-join.reason", autoOpenJoinReasons);

        Map<String, String> map = lastStartTimes.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().map(Object::toString).collect(Collectors.joining(","))
                ));
        config.set("last-start-time", map);
        return super.save();
    }
}
