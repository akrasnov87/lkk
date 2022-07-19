package ru.mobnius.cic.ui.model.concurent;

import androidx.annotation.NonNull;

public class AppVersionResult {
    public final boolean isError;
    @NonNull
    public final String updateVersion;

    public AppVersionResult(final @NonNull String updateVersion, final boolean isError) {
        this.isError = isError;
        this.updateVersion = updateVersion;
    }
}
