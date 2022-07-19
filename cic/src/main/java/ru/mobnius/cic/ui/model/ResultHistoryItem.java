package ru.mobnius.cic.ui.model;

import androidx.annotation.NonNull;

public class ResultHistoryItem {
    @NonNull
    public final String causeName;
    @NonNull
    public final String statusName;
    @NonNull
    public final String date;
    public final long time;

    public ResultHistoryItem(final @NonNull String causeName,
                             final @NonNull String statusName,
                             final @NonNull String date,
                             final long time) {
        this.causeName = causeName;
        this.statusName = statusName;
        this.date = date;
        this.time = time;
    }
}
