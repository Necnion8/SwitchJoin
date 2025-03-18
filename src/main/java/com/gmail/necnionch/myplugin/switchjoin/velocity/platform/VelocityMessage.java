package com.gmail.necnionch.myplugin.switchjoin.velocity.platform;

import com.gmail.necnionch.myplugin.switchjoin.common.util.Message;
import net.kyori.adventure.text.Component;

public record VelocityMessage(Component component) implements Message {

    public static VelocityMessage of(Component component) {
        return new VelocityMessage(component);
    }

}
