package com.gmail.necnionch.myplugin.switchjoin.common.events;

import com.gmail.necnionch.myplugin.switchjoin.common.util.Message;
import org.jetbrains.annotations.Nullable;

public record EventResultBroadcast<M extends Message>(EventResult result, @Nullable M message) {
}
