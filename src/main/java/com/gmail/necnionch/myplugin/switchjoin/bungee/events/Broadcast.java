package com.gmail.necnionch.myplugin.switchjoin.bungee.events;

import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.Nullable;

public interface Broadcast {

    @Nullable
    BaseComponent[] getBroadcastMessage();

    void setBroadcastMessage(@Nullable BaseComponent[] message);

}
