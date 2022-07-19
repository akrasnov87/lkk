package ru.mobnius.simple_core.data.zip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ZipResult {

    @NonNull
    private final byte[] origin;
    @Nullable
    private byte[] compress;
    private double k;


    public ZipResult(final @NonNull byte[] origin) {
        this.origin = origin;
    }

    @NonNull
    public ZipResult getResult(final @NonNull byte[] compress) {
        this.compress = compress;
        this.k = (double) (compress.length * 100) / this.origin.length;
        return this;
    }

    @NonNull
    public byte[] getCompress() {
        if (this.compress == null){
            return new byte[0];
        }
        return this.compress;
    }

    public double getK() {
        return this.k;
    }
}
