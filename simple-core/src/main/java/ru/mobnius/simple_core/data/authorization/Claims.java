package ru.mobnius.simple_core.data.authorization;

import androidx.annotation.NonNull;

public class Claims {
    /**
     * наименование роли
     */
    @NonNull
    public final String name;

    public Claims(final @NonNull String name) {
        this.name = name;
    }
}
