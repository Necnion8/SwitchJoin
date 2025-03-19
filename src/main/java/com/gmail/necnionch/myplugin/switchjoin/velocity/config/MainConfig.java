package com.gmail.necnionch.myplugin.switchjoin.velocity.config;

import com.gmail.necnionch.myplugin.switchjoin.common.config.SwitchJoinConfig;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MainConfig extends VelocityConfigDriver implements SwitchJoinConfig {
    private List<String> autoOpenJoinReasons = Collections.emptyList();
    private final Map<String, List<Integer>> lastStartTimes = Collections.synchronizedMap(new HashMap<>());

    public MainConfig(Logger logger, Path dataFolder) {
        super(logger, dataFolder);
    }


    @Override
    public boolean isAutoOpenJoin() {
        return config.node("auto-open-join", "enable").getBoolean(false);
    }

    @Override
    public List<String> getAutoOpenJoinReasons() {
        return autoOpenJoinReasons;
    }

    @Override
    public String getMessageFailKick() {
        return config.node("auto-open-join", "fail-to-kick-message").getString();
    }

    @Override
    public boolean isAutoCloseEmpty() {
        return getAutoCloseEmptySection().node("enable").getBoolean(false);
    }

    @Override
    public int getAutoCloseTimerMinutes() {
        return getAutoCloseEmptySection().node("timer-minutes").getInt(120);
    }

    @Override
    public int getAutoCloseNotifyMinutes() {
        return getAutoCloseEmptySection().node("notify-minutes").getInt(5);
    }

    @Override
    public String getSwitcherServerId(String serverName) {
        return config.node("servers", serverName).getString();
    }

    @Override
    public String getPlatformServerId(String switcherServerName) {
        for (CommentedConfigurationNode value : config.node("servers").childrenList()) {
            String name = value.getString();
            if (switcherServerName.equals(name)) {
                return name;
            }
        }
        return null;
    }

    @Override
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

    @Override
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

    @Override
    public String getSlpStatusName(String key) {
        return config.node("serverlistplus", "status", key.toUpperCase()).getString();
    }

    // setter

    @Override
    public void setIsAutoOpenJoin(boolean enable) {
        try {
            config.node("auto-open-join", "enable").set(enable);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
        save();
    }

    @Override
    public boolean addAutoJoinReason(String reason) {
        Set<String> set = new HashSet<>(autoOpenJoinReasons);
        boolean result = set.add(reason.toUpperCase());
        autoOpenJoinReasons.clear();
        autoOpenJoinReasons.addAll(set);
        save();
        return result;
    }

    @Override
    public boolean removeAutoJoinReason(String reason) {
        Set<String> set = new HashSet<>(autoOpenJoinReasons);
        boolean result = set.remove(reason.toUpperCase());
        autoOpenJoinReasons.clear();
        autoOpenJoinReasons.addAll(set);
        save();
        return result;
    }

    @Override
    public void setIsAutoCloseEmpty(boolean enable) {
        try {
            getAutoCloseEmptySection().node("enable").set(enable);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
        save();
    }

    @Override
    public void setAutoCloseTimerMinutes(int minutes) {
        try {
            getAutoCloseEmptySection().node("timer-minutes").set(minutes);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
        save();
    }

    @Override
    public void setAutoCloseNotifyMinutes(int minutes) {
        try {
            getAutoCloseEmptySection().node("notify-minutes").set(minutes);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
        save();
    }

    @Override
    public void addServer(String bungeeName, String switcherName) {
        try {
            config.node("servers", bungeeName).set(switcherName);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
        save();
    }

    @Override
    public void removeServer(String bungeeName) {
        try {
            config.node("servers", bungeeName).set(null);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
        save();
    }

    @Override
    public String[] getPlatformServers() {
        return config.node("servers").childrenList().stream().map(String::valueOf).toArray(String[]::new);
    }

    private ConfigurationNode getAutoCloseEmptySection() {
        return config.node("auto-close-empty");
    }



    @Override
    public boolean onLoaded(CommentedConfigurationNode config) {
        try {
            autoOpenJoinReasons = config.node("auto-open-join", "reason").childrenList().stream()
                    .map(c -> Optional.ofNullable(c.getString()).map(s -> s.toUpperCase(Locale.ROOT)).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            lastStartTimes.clear();

            config.node("last-start-time").childrenMap().forEach((k, node) -> {
                String serverId = (String) k;
                String flat = node.getString();
                if (flat == null)
                    return;

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
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean save() {
        try {
            config.node("auto-open-join", "reason").set(autoOpenJoinReasons);

            Map<String, String> map = lastStartTimes.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().stream().map(Object::toString).collect(Collectors.joining(","))
                    ));

            config.node("last-start-time").set(map);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
        return super.save();
    }
}
