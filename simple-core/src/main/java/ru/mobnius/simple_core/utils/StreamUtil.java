package ru.mobnius.simple_core.utils;

import androidx.annotation.NonNull;

import org.apache.commons.io.output.ThresholdingOutputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.mobnius.simple_core.BaseApp;

public class StreamUtil {

    public static final int MIDDLE_BYTE_BUFFER = 4096;
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final int EOF = -1;

    @NonNull
    public static byte[] toByteArray(final @NonNull InputStream inputStream) throws IOException {
        try (final UnsynchronizedByteArrayOutputStream ubaOutput = new UnsynchronizedByteArrayOutputStream();
             final ThresholdingOutputStream thresholdOuput = new ThresholdingOutputStream(Integer.MAX_VALUE, os -> {
                 throw new IllegalArgumentException(BaseApp.ERROR_ENG + Integer.MAX_VALUE);
             }, os -> ubaOutput)) {
            copy(inputStream, thresholdOuput);
            inputStream.close();
            return ubaOutput.toByteArray();
        }
    }

    public static void copy(final @NonNull InputStream inputStream, final @NonNull OutputStream outputStream) throws IOException {
        copyLarge(inputStream, outputStream);

    }

    public static void copyLarge(final @NonNull InputStream inputStream, final @NonNull OutputStream outputStream) throws IOException {
        copy(inputStream, outputStream, DEFAULT_BUFFER_SIZE);
    }

    public static void copy(final @NonNull InputStream inputStream, final @NonNull OutputStream outputStream, final int bufferSize)
            throws IOException {
        copyLarge(inputStream, outputStream, new byte[bufferSize]);
    }

    public static void copyLarge(final @NonNull InputStream inputStream, final @NonNull OutputStream outputStream, final @NonNull byte[] buffer)
            throws IOException {
        int n;
        while (EOF != (n = inputStream.read(buffer))) {
            outputStream.write(buffer, 0, n);
        }
    }

}
