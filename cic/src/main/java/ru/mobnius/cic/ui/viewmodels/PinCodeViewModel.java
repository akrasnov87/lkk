package ru.mobnius.cic.ui.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.mobnius.cic.ui.pincode.factory.ExistPinCodeResult;
import ru.mobnius.cic.ui.pincode.factory.NewPinCodeResult;
import ru.mobnius.cic.ui.pincode.factory.PinCodeResult;
import ru.mobnius.simple_core.data.configuration.PreferencesManager;
import ru.mobnius.simple_core.utils.StringUtil;

public class PinCodeViewModel extends ViewModel {
    public String pinCode = StringUtil.EMPTY;
    public String tempPin = StringUtil.EMPTY;
    public boolean isCreate;

    private MutableLiveData<PinCodeResult> pinCodeLiveData;
    private MutableLiveData<PinCodeResult> clearDigitLiveData;

    @NonNull
    public LiveData<PinCodeResult> refreshPinCode(final @NonNull String digit) {
        if (pinCodeLiveData == null) {
            pinCodeLiveData = new MutableLiveData<>();
        }
        if (isCreate) {
            pinCode += digit;

            if (pinCode.length() > PinCodeResult.MAX_PIN_LENGTH) {
                pinCode = pinCode.substring(0, PinCodeResult.MAX_PIN_LENGTH);
            }

            if (pinCode.length() != PinCodeResult.MAX_PIN_LENGTH) {
                pinCodeLiveData.setValue(new NewPinCodeResult(pinCode, tempPin, pinCode.length() - 1));
                return pinCodeLiveData;
            }

            if (tempPin.isEmpty()) {
                tempPin = pinCode;
                pinCode = StringUtil.EMPTY;
                pinCodeLiveData.setValue(new NewPinCodeResult(pinCode, tempPin, PinCodeResult.MAX_PIN_LENGTH));
            } else {
                if (tempPin.equals(pinCode)) {
                    if (PreferencesManager.getInstance() != null) {
                        PreferencesManager.getInstance().setPinAuth(true);
                        PreferencesManager.getInstance().setPinCode(pinCode);
                    }
                    pinCodeLiveData.setValue(new NewPinCodeResult(pinCode, tempPin, PinCodeResult.MAX_PIN_LENGTH));
                } else {
                    pinCodeLiveData.setValue(new NewPinCodeResult(pinCode, tempPin, PinCodeResult.MAX_PIN_LENGTH));
                    tempPin = StringUtil.EMPTY;
                    pinCode = StringUtil.EMPTY;
                }
            }

        } else {
            if (pinCode.length() < PinCodeResult.MAX_PIN_LENGTH) {
                pinCode += digit;
            }
            pinCodeLiveData.setValue(new ExistPinCodeResult(pinCode, pinCode.length() - 1));
            if (pinCode.length() == PinCodeResult.MAX_PIN_LENGTH) {
                pinCode = StringUtil.EMPTY;
            }
        }
        return pinCodeLiveData;
    }

    @NonNull
    public LiveData<PinCodeResult> clearOneDigit() {
        if (clearDigitLiveData == null) {
            clearDigitLiveData = new MutableLiveData<>();
        }
        if (pinCode.length() > 1) {
            pinCode = pinCode.substring(0, pinCode.length() - 1);
        } else {
            pinCode = StringUtil.EMPTY;
        }
        if (isCreate) {
            clearDigitLiveData.setValue(new NewPinCodeResult(pinCode, tempPin, pinCode.length() + 1));
        } else {
            clearDigitLiveData.setValue(new ExistPinCodeResult(pinCode, pinCode.length() + 1));
        }

        return clearDigitLiveData;
    }
}
