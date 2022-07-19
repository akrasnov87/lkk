package ru.mobnius.cic.ui.verification;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.component.CicSpinner;

public class CicSpinnerVerification implements VerifiableField<CicSpinner> {
    @NonNull
    private final CicSpinner cicSpinner;
    private int order = 0;

    public CicSpinnerVerification(@NotNull CicSpinner cicSpinner) {
        this.cicSpinner = cicSpinner;
    }

    @NonNull
    @Override
    public @NotNull
    CicSpinner getView() {
        return cicSpinner;
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
        cicSpinner.setError(null);
        if (cicSpinner.isSelected()) {
            return true;
        }
        cicSpinner.setError(cicSpinner.getContext().getString(R.string.must_make_choise));
        return false;
    }

    @Override
    public boolean isClearable() {
        return false;
    }

    @Override
    public boolean validateInvisible() {
        return cicSpinner.isSelected();
    }
}
