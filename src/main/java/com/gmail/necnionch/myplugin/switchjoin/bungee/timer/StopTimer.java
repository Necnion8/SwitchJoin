package com.gmail.necnionch.myplugin.switchjoin.bungee.timer;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myplugin.switchjoin.bungee.SwitchJoin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class StopTimer {
    private static OnTimeListener timeListener = (s, ss) -> {};
    private static OnPreTimeListener preTimeListener = (s, ss, r) -> {};

    private final SwitcherServer switcherServer;
    private final ServerInfo bungeeServer;
    private int minutes = 0;
    private int notifyMinutes = 0;
    private Long timerStarted = null;
    private boolean notifyTurn = false;
    private ScheduledTask task = null;

    public static void setListener(OnTimeListener onTimeListener, OnPreTimeListener onPreTimeListener) {
        StopTimer.timeListener = onTimeListener;
        StopTimer.preTimeListener = onPreTimeListener;
    }

    public StopTimer(SwitcherServer switcherServer, ServerInfo bungeeServer) {
        this.switcherServer = switcherServer;
        this.bungeeServer = bungeeServer;
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
//        task = createTask();
//        timer.schedule(task, ((notifyTurn) ? minutes - notifyMinutes : minutes) * 60000);

        task = ProxyServer.getInstance().getScheduler().schedule(SwitchJoin.getInstance(),
                createTask(), (notifyTurn) ? minutes - notifyMinutes : minutes, TimeUnit.MINUTES);

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

    public ServerInfo getBungeeServer() {
        return bungeeServer;
    }

    public SwitcherServer getSwitcherServer() {
        return switcherServer;
    }

    private Runnable createTask() {
        return () -> {
            task = null;
            if (notifyTurn) {
                preTimeListener.onPreTime(bungeeServer, switcherServer, notifyMinutes);
                notifyTurn = false;

                task = ProxyServer.getInstance().getScheduler().schedule(SwitchJoin.getInstance(),
                        createTask(), notifyMinutes, TimeUnit.MINUTES);
//                timer.schedule(task, notifyMinutes * 60000);

            } else {
                stop();
                timeListener.onTime(bungeeServer, switcherServer);
            }
        };
    }



    public interface OnTimeListener {
        void onTime(ServerInfo s, SwitcherServer ss);
    }

    public interface OnPreTimeListener {
        void onPreTime(ServerInfo s, SwitcherServer ss, int remainingMinutes);
    }

}
