package ru.mobnius.cic.ui.pincode;

import androidx.annotation.NonNull;

/**
 * Обратные вызовы нажатия на кнопки экрана ввода пин-кода
 */
public interface PinCodeDigitsCallback {

    void onDigitEntered(final @NonNull String digit);

    void onBackspacePressed();

    void onPinDropConfirmed();

}
