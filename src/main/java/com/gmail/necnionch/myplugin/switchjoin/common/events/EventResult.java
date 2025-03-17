package com.gmail.necnionch.myplugin.switchjoin.common.events;

public enum EventResult {
    ALLOW(false), DENY(true);

    private final boolean denied;

    EventResult(boolean denied) {
        this.denied = denied;
    }

    public boolean isDenied() {
        return denied;
    }

}
