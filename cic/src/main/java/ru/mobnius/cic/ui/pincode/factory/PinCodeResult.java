package ru.mobnius.cic.ui.pincode.factory;

import androidx.annotation.NonNull;

/**
 * Интерфейс представляющий экран ввода пин-кода
 */

public interface PinCodeResult {
    int MAX_PIN_LENGTH = 4;

    boolean isNewPinCode();

    @NonNull
    String pinCode();

    boolean enterPinAgain();

    boolean pinsNotEqual();

    boolean continueEnter();
}
