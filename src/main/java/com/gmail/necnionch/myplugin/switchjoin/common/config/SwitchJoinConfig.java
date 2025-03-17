package com.gmail.necnionch.myplugin.switchjoin.common.config;

import java.util.List;

public interface SwitchJoinConfig {

    boolean isAutoOpenJoin();

    List<String> getAutoOpenJoinReasons();

    String getMessageFailKick();

    boolean isAutoCloseEmpty();

    int getAutoCloseTimerMinutes();

    int getAutoCloseNotifyMinutes();

    String getSwitcherServerId(String serverName);

    String getPlatformServerId(String switcherName);

    void putStartTime(String serverId, int startTime);

    Integer getStartTime(String serverId);

    String getSlpStatusName(String key);

    void setIsAutoOpenJoin(boolean enable);

    boolean addAutoJoinReason(String reason);

    boolean removeAutoJoinReason(String reason);

    void setIsAutoCloseEmpty(boolean enable);

    void setAutoCloseTimerMinutes(int minutes);

    void setAutoCloseNotifyMinutes(int minutes);

    void addServer(String platformName, String switcherName);

    void removeServer(String platformName);

    String[] getPlatformServers();

}
