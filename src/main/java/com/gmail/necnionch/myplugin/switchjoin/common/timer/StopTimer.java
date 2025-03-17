package com.gmail.necnionch.myplugin.switchjoin.common.timer;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myplugin.switchjoin.common.platform.Platform;
import com.gmail.necnionch.myplugin.switchjoin.common.platform.PlatformServer;
import com.gmail.necnionch.myplugin.switchjoin.common.util.Message;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public class StopTimer<P extends Platform<M, S>, M extends Message, S extends PlatformServer> {

    private final P platform;
    private final SwitcherServer switcherServer;
    private final S platformServer;
    private final Listener<S> listener;
    private int minutes = 0;
    private int notifyMinutes = 0;
    private Long timerStarted = null;
    private boolean notifyTurn = false;
    private @Nullable Platform.Task task;

    public StopTimer(P platform, SwitcherServer switcherServer, S platformServer, Listener<S> listener) {
        this.platform = platform;
        this.switcherServer = switcherServer;
        this.platformServer = platformServer;
        this.listener = listener;
    }

    public void setMinutes(int minutes, int notifyMinutes) {
        this.minutes = minutes;
        this.notifyMinutes = notifyMinutes;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getNotifyMinutes() {
        return notifyMinutes;
    }

    public void start() {
        if (minutes <= 0)
            return;

        stop();
        notifyTurn = minutes > notifyMinutes && notifyMinutes > 0;
        timerStarted = System.currentTimeMillis();
        task = platform.runTaskLater(this::doTime, (notifyTurn) ? minutes - notifyMinutes : minutes, TimeUnit.MINUTES);
    }

    public boolean stop() {
        notifyTurn = false;
        timerStarted = null;
        if (task != null) {
            task.cancel();
            task = null;
            return true;
        }
        return false;
    }

    public boolean isRunning() {
        return task != null;
    }

    public Long getTimerStartedTime() {
        return timerStarted;
    }

    public S getPlatformServer() {
        return platformServer;
    }

    public SwitcherServer getSwitcherServer() {
        return switcherServer;
    }

    private void doTime() {
        task = null;
        if (notifyTurn) {
            listener.onPreTime(platformServer, switcherServer, notifyMinutes);
            notifyTurn = false;
            task = platform.runTaskLater(this::doTime, notifyMinutes, TimeUnit.MINUTES);

        } else {
            stop();
            listener.onTime(platformServer, switcherServer);
        }
    }


    public interface Listener<S extends PlatformServer> {

        void onTime(S s, SwitcherServer ss);

        void onPreTime(S s, SwitcherServer ss, int remainingMinutes);

    }

}
