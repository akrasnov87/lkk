package ru.mobnius.simple_core.data.synchronization;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface OnAttachmentListeners {
    @Nullable
    String getAbsoluteFilePath();
    boolean getIsDelete();
    @NonNull
    String getId();
}
