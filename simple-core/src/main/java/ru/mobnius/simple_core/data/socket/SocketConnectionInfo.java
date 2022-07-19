package ru.mobnius.simple_core.data.socket;

import androidx.annotation.NonNull;

public class SocketConnectionInfo {
    public final boolean connecting;
    public final boolean connectionError;
    @NonNull
    public final String message;

    public SocketConnectionInfo(final boolean connecting, final boolean connectionError, final @NonNull String message) {
        this.connecting = connecting;
        this.connectionError = connectionError;
        this.message = message;
    }
}
