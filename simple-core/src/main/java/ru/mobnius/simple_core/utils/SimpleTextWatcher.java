package ru.mobnius.simple_core.utils;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.Nullable;

/**
 * Класс для более удобного использования {@link TextWatcher}
 * позволяет имплементировать только те методы которые нужны
 */
public abstract class SimpleTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(final @Nullable CharSequence s, final int start, final int count, final int after) {

    }

    @Override
    public void onTextChanged(final @Nullable CharSequence s, final int start, final int before, final int count) {

    }

    @Override
    public void afterTextChanged(final @Nullable Editable s) {

    }
}
