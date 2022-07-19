package ru.mobnius.cic.ui.verification;

import androidx.annotation.NonNull;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.component.CicEditText;
import ru.mobnius.simple_core.utils.StringUtil;

public class CicEditEmptyVerification implements VerifiableField<CicEditText> {
    @NonNull
    private final CicEditText cicEditText;
    private int order;
    private boolean isClearable = false;

    public CicEditEmptyVerification(@NonNull CicEditText cicEditText) {
        this.cicEditText = cicEditText;
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
        if (StringUtil.isEmpty(cicEditText.getText())) {
            cicEditText.setError(cicEditText.getContext().getString(R.string.must_to_be_not_empty));
            return false;
        }
        return true;
    }

    public void setClearable(boolean clearable) {
        isClearable = clearable;
    }

    @Override
    public boolean isClearable() {
        return isClearable;
    }

    @Override
    public boolean validateInvisible() {
        return StringUtil.isNotEmpty(cicEditText.getText());
    }
}
