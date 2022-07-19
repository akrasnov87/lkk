package ru.mobnius.simple_core.data.synchronization;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.mobnius.simple_core.data.synchronization.utils.transfer.Transfer;

public class EndTransferResult {
    @NonNull
    public final String tid;
    @NonNull
    public final Transfer transfer;
    @Nullable
    public final Object object;
    public EndTransferResult(final @NonNull String tid, final @NonNull Transfer transfer, final @Nullable Object object) {
        this.tid = tid;
        this.object = object;
        this.transfer = transfer;
    }

}
