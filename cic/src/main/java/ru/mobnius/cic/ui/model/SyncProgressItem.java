package ru.mobnius.cic.ui.model;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.TestOnly;

import java.util.UUID;

import ru.mobnius.simple_core.data.configuration.PreferencesManager;
import ru.mobnius.simple_core.utils.StringUtil;

public class SyncProgressItem {
    public int uploadProgress;
    public int downloadProgress;
    public int type;
    @NonNull
    public String name;
    @NonNull
    public String status;
    @NonNull
    public String transferData;
    @NonNull
    public String tid;


    public SyncProgressItem(final @NonNull String tid,
                            final int type,
                            final int uploadProgress,
                            final int downloadProgress,
                            final @NonNull String name,
                            final @NonNull String transferData,
                            final @NonNull String status) {
        this.uploadProgress = uploadProgress;
        this.name = name;
        this.status = status;
        this.downloadProgress = downloadProgress;
        this.tid = tid;
        this.type = type;
        this.transferData = transferData;
    }

    public void update(SyncProgressItem item) {
        this.uploadProgress = item.uploadProgress;
        this.name = item.name;
        this.status = item.status;
        this.downloadProgress = item.downloadProgress;
        this.tid = item.tid;
        this.type = item.type;
        this.transferData = item.transferData;
    }

    @TestOnly
    public static SyncProgressItem getTestInstanse(){
        return new SyncProgressItem(UUID.randomUUID().toString(),
                1,
                1,
                1,
                StringUtil.EMPTY,
                StringUtil.EMPTY,
                StringUtil.EMPTY);
    }
}
