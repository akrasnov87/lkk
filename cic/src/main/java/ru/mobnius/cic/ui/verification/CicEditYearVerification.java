package ru.mobnius.cic.ui.verification;

import androidx.annotation.NonNull;

import ru.mobnius.cic.ui.component.CicEditText;

public class CicEditYearVerification implements VerifiableField<CicEditText> {
    public final static int PAST = 0;
    public final static int FUTUTRE = 1;
    public final static int ANY = 2;
    @NonNull
    private final CicEditText cicEditText;
    private int order;
    private final int mode;

    public CicEditYearVerification(@NonNull CicEditText cicEditText, int mode) {
        this.cicEditText = cicEditText;
        this.mode = mode;
    }

    @NonNull
    @Override
    public CicEditText getView() {
        return cicEditText;
    }

    @Override
    public int getValidSoftType() {
        return 0;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public boolean validateSoft() {
        return true;
    }

    @Override
    public boolean validateSolid() {
        switch (mode) {
            case PAST:
                return CicEditValidationUtil.validateIsPastYear(cicEditText.getText(), cicEditText, true);
            case FUTUTRE:
                return CicEditValidationUtil.validateIsFutureYear(cicEditText.getText(), cicEditText, true);
            case ANY:
                return CicEditValidationUtil.validateYear(cicEditText.getText(), cicEditText, true);
            default:
                return false;
        }
    }

    @Override
    public boolean isClearable() {
        return false;
    }

    @Override
    public boolean validateInvisible() {
        switch (mode) {
            case PAST:
                return CicEditValidationUtil.validateIsPastYear(cicEditText.getText(), cicEditText, false);
            case FUTUTRE:
                return CicEditValidationUtil.validateIsFutureYear(cicEditText.getText(), cicEditText, false);
            case ANY:
                return CicEditValidationUtil.validateYear(cicEditText.getText(), cicEditText, false);
            default:
                return false;
        }
    }
}