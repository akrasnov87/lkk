package ru.mobnius.cic.ui.pincode.factory;

import androidx.annotation.NonNull;

/**
 * Класс представляющий экран для ввода существующего пин-кода
 */
public class ExistPinCodeResult implements PinCodeResult {
    @NonNull
    public final String pinCode;
    public final int previous;

    public ExistPinCodeResult(final @NonNull String pinCode, final int previous) {
        this.pinCode = pinCode;
        this.previous = previous;
    }

    @Override
    public boolean isNewPinCode() {
        return false;
    }

    @NonNull
    @Override
    public String pinCode() {
        return pinCode;
    }

    @Override
    public boolean enterPinAgain() {
        return false;
    }

    @Override
    public boolean pinsNotEqual() {
        return false;
    }

    @Override
    public boolean continueEnter() {
        return pinCode.length() < MAX_PIN_LENGTH;
    }
}
