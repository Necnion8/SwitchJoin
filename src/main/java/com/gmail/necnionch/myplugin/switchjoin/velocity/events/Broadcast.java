package com.gmail.necnionch.myplugin.switchjoin.velocity.events;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public interface Broadcast {

    @Nullable
    Component getBroadcastMessage();

    void setBroadcastMessage(@Nullable Component message);

}
