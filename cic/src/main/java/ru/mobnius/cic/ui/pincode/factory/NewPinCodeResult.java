package ru.mobnius.cic.ui.pincode.factory;

import androidx.annotation.NonNull;

import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Класс представляющий экран для создания нового пин-кода
 */
public class NewPinCodeResult implements PinCodeResult {
    @NonNull
    public final String pinCode;
    @NonNull
    public final String tempPinCode;
    public final int previous;

    public NewPinCodeResult(final @NonNull String pinCode, final @NonNull String tempPinCode, final int previous) {
        this.pinCode = pinCode;
        this.tempPinCode = tempPinCode;
        this.previous = previous;
    }

    @Override
    public boolean isNewPinCode() {
        return true;
    }

    @NonNull
    @Override
    public String pinCode() {
        return pinCode;
    }

    @Override
    public boolean enterPinAgain() {
        return StringUtil.isEmpty(pinCode) && StringUtil.isNotEmpty(tempPinCode);
    }

    @Override
    public boolean pinsNotEqual() {
        return pinCode.length() == MAX_PIN_LENGTH && tempPinCode.length() == MAX_PIN_LENGTH && !StringUtil.equals(tempPinCode, pinCode);
    }

    @Override
    public boolean continueEnter() {
        return pinCode.length() < MAX_PIN_LENGTH;
    }
}
