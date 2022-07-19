package ru.mobnius.cic.ui.model;

import androidx.annotation.NonNull;

public class ProfileItem {
    @NonNull
    public final String fio;

    public ProfileItem(final @NonNull String fio) {
        this.fio = fio;
    }
}
