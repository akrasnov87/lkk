package ru.mobnius.simple_core.data.packager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class MetaPackage {
    @NonNull
    @Expose
    public final String id;
    @NonNull
    @Expose
    public final MetaAttachment[] attachments;
    @NonNull
    @Expose
    public final String dataInfo;
    @NonNull
    @Expose
    public final String version;
    @Expose
    public final int binarySize;
    @Expose
    public final int bufferBlockFromLength;
    @Expose
    public final int bufferBlockToLength;
    @Expose
    public final int stringSize;
    @Expose
    public final boolean transaction;

    public MetaPackage(final @NonNull String tid,
                       final @NonNull MetaAttachment[] attachments,
                       final @NonNull String dataInfo,
                       final @NonNull String version,
                       final int binarySize,
                       final int bufferBlockFromLength,
                       final int bufferBlockToLength,
                       final int stringSize,
                       final boolean transaction) {
        this.id = tid;
        this.attachments = attachments;
        this.dataInfo = dataInfo;
        this.version = version;
        this.binarySize = binarySize;
        this.bufferBlockFromLength = bufferBlockFromLength;
        this.bufferBlockToLength = bufferBlockToLength;
        this.stringSize = stringSize;
        this.transaction = transaction;
    }

    public String toJsonString() {
        return new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create().toJson(this);
    }
}
