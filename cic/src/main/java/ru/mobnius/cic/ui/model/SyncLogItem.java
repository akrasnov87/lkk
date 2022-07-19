package ru.mobnius.cic.ui.model;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.TestOnly;

import java.util.Date;
import java.util.UUID;

import ru.mobnius.cic.MobniusApplication;

public class SyncLogItem {
    @NonNull
    public final Date d_date;
    @NonNull
    public final String c_message;
    public final boolean b_error;

    public SyncLogItem(final @NonNull String message, final boolean isError) {
        d_date = new Date();
        c_message = message;
        b_error = isError;
    }

    @TestOnly
    public static SyncLogItem getTestInstanse() {
        return new SyncLogItem(MobniusApplication.ERROR, false);
    }
}
