package ru.mobnius.cic.ui.model;

import androidx.annotation.NonNull;

public class FailureImageObject {
    public final long failureId;
    public final long imageTypeId;
    public final int count;
    public final String name;

    public FailureImageObject(final long failureId,
                              final long imageTypeId,
                              final int count,
                              final @NonNull String name) {
        this.failureId = failureId;
        this.imageTypeId = imageTypeId;
        this.count = count;
        this.name = name;
    }

}
