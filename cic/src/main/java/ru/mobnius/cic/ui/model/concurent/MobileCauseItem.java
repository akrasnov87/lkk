package ru.mobnius.cic.ui.model.concurent;

import androidx.annotation.NonNull;

import ru.mobnius.cic.data.storage.models.Causes;
import ru.mobnius.simple_core.utils.LongUtil;
import ru.mobnius.simple_core.utils.StringUtil;

public class MobileCauseItem {
    @NonNull
    public String causeName;

    public long mobileCauseId;

    public MobileCauseItem() {
        this.mobileCauseId = LongUtil.MINUS;
        causeName = StringUtil.EMPTY;
    }

    public void initCauseId(final @NonNull Causes cause) {
        this.mobileCauseId = LongUtil.getLongOrMinus(cause.id);
        this.causeName = StringUtil.defaultEmptyString(cause.c_name);
    }

    public boolean isFailureNotSelected() {
        return mobileCauseId == LongUtil.MINUS;
    }

}
