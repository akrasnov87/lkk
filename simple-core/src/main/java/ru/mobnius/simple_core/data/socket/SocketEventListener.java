package ru.mobnius.simple_core.data.socket;

import androidx.annotation.NonNull;

public interface SocketEventListener {
    void onConnect();
    void onConnectError(final @NonNull String error);
    void onDisconnect();
}
