package ru.mobnius.cic.ui.model;

import androidx.annotation.NonNull;

public class SyncPercentageProgress {
    public final int progress;
    public final boolean isUpload;
    public final int step;
    public final boolean isError;
    public final String errorMessage;

    public SyncPercentageProgress(final int step,
                                  final int progress,
                                  final boolean isUpload,
                                  final boolean isError,
                                  final @NonNull String errorMessage) {
        this.progress = progress;
        this.isUpload = isUpload;
        this.step = step;
        this.isError = isError;
        this.errorMessage = errorMessage;
    }

    public String getPercentage(int maxValue) {
        return String.valueOf(100 * progress / maxValue) + "%";
    }


}
