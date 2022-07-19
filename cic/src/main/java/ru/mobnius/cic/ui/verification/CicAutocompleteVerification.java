package ru.mobnius.cic.ui.verification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.mobnius.cic.ui.component.CicAutocompleteFieldView;

public class CicAutocompleteVerification implements VerifiableField<CicAutocompleteFieldView> {
    @NonNull
    private final CicAutocompleteFieldView autocompleteFieldView;
    private int order;

    public CicAutocompleteVerification(@NonNull CicAutocompleteFieldView autocompleteFieldView) {
        this.autocompleteFieldView = autocompleteFieldView;
    }

    @NonNull
    @Override
    public CicAutocompleteFieldView getView() {
        return autocompleteFieldView;
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
        if (autocompleteFieldView.isSelected()) {
            autocompleteFieldView.clearError();
            return true;
        }
        autocompleteFieldView.setError();
        return false;
    }

    @Override
    public boolean isClearable() {
        return false;
    }

    @Override
    public boolean validateInvisible() {
        return autocompleteFieldView.isSelected();
    }
}
