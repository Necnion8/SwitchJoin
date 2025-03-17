package com.gmail.necnionch.myplugin.switchjoin.bungee.platform;

import com.gmail.necnionch.myplugin.switchjoin.common.util.Message;
import net.md_5.bungee.api.chat.BaseComponent;

public record BungeeMessage(BaseComponent[] components) implements Message {

    public static BungeeMessage of(BaseComponent[] components) {
        return new BungeeMessage(components);
    }

}
