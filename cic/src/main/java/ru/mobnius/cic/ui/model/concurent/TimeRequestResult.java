package ru.mobnius.cic.ui.model.concurent;

import androidx.annotation.NonNull;

public class TimeRequestResult {

    public final boolean isError;
    @NonNull
    public final String message;

    public TimeRequestResult(final @NonNull String message, final boolean isError) {
        this.message = message;
        this.isError = isError;
    }
}
