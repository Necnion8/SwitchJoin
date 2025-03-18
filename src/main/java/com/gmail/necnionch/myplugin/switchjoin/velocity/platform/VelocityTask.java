package com.gmail.necnionch.myplugin.switchjoin.velocity.platform;

import com.gmail.necnionch.myplugin.switchjoin.common.platform.Platform;
import com.velocitypowered.api.scheduler.ScheduledTask;

public record VelocityTask(ScheduledTask task) implements Platform.Task {
    @Override
    public void cancel() {
        task.cancel();
    }
}
