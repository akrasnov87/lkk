package ru.mobnius.simple_core.data;

import androidx.annotation.NonNull;

public class Meta {

    public static final int OK = 200;
    public static final int NOT_FOUND = 404;
    public static final int ERROR_SERVER = 500;
    public static final int NOT_AUTHORIZATION = 401;

    public final int status;
    @NonNull
    public final String message;

    public Meta(final int status, final @NonNull String message) {
        this.status = status;
        this.message = message;
    }

    public boolean isSuccess() {
        return status == OK;
    }
}
