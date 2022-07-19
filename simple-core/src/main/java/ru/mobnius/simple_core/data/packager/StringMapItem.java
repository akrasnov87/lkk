package ru.mobnius.simple_core.data.packager;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

public class StringMapItem {
    @NonNull
    @Expose
    public final String name;
    @Expose
    public final int length;

    public StringMapItem(final @NonNull String name, final int length) {
        this.name = name;
        this.length = length;
    }
}
