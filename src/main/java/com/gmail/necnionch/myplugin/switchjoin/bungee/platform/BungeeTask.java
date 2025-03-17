package com.gmail.necnionch.myplugin.switchjoin.bungee.platform;

import com.gmail.necnionch.myplugin.switchjoin.common.platform.Platform;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public record BungeeTask(ScheduledTask task) implements Platform.Task {
    @Override
    public void cancel() {
        task.cancel();
    }
}
