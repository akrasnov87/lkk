package ru.mobnius.simple_core.data.zip;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import ru.mobnius.simple_core.utils.FileUtil;
import ru.mobnius.simple_core.utils.StreamUtil;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Сжатие строки через ZIP
 */
public class ZipManager {

    @NonNull
    public static String getMode() {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.M ? "LIB" : "ZIP";
    }

    /**
     * Сжать информацию
     *
     * @param inputText текст
     * @return массив байтов
     */
    @NonNull
    public static ZipResult compress(final @NonNull String inputText) throws IOException {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            return oldCompress(inputText);
        } else {
            return newCompress(inputText);
        }
    }

    /**
     * Распаковать информацию
     *
     * @param compressed данные
     * @return строка с информацией
     * @throws IOException исключение при обработке
     */
    @Nullable
    public static byte[] decompress(final @NonNull byte[] compressed) throws IOException, DataFormatException {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            return oldDeCompress(compressed);
        } else {
            return newDeCompress(compressed);
        }
    }

    @NonNull
    private static ZipResult oldCompress(final @NonNull String inputText) {
        final byte[] input = inputText.getBytes(StandardCharsets.UTF_8);
        final ZipResult zipResult = new ZipResult(input);

        final Deflater compressor = new Deflater();
        compressor.setInput(input);
        compressor.finish();

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final byte[] buf = new byte[StreamUtil.MIDDLE_BYTE_BUFFER];
        while (!compressor.finished()) {
            final int byteCount = compressor.deflate(buf);
            byteArrayOutputStream.write(buf, 0, byteCount);
        }
        compressor.end();
        return zipResult.getResult(byteArrayOutputStream.toByteArray());
    }

    @NonNull
    private static ZipResult newCompress(final @NonNull String inputText) throws IOException {
        final byte[] bytes = inputText.getBytes(StandardCharsets.UTF_8);

        final ZipResult zipResult = new ZipResult(bytes);

        final InputStream stream = new ByteArrayInputStream(bytes);
        final byte[] data = new byte[StreamUtil.MIDDLE_BYTE_BUFFER];
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ZipOutputStream zos = new ZipOutputStream(bos);
        final BufferedInputStream entryStream = new BufferedInputStream(stream, StreamUtil.MIDDLE_BYTE_BUFFER);
        final ZipEntry entry = new ZipEntry(StringUtil.EMPTY);
        zos.putNextEntry(entry);
        int count;
        while ((count = entryStream.read(data, 0, StreamUtil.MIDDLE_BYTE_BUFFER)) != -1) {
            zos.write(data, 0, count);
        }
        entryStream.close();
        zos.closeEntry();
        zos.close();

        return zipResult.getResult(bos.toByteArray());
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    @Nullable
    private static byte[] newDeCompress(final @NonNull byte[] compressed) throws IOException {
        final ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        final ZipInputStream zin = new ZipInputStream(bis);

        while (zin.getNextEntry() != null) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final byte[] data = new byte[StreamUtil.MIDDLE_BYTE_BUFFER];
            int count;
            while ((count = zin.read(data, 0, StreamUtil.MIDDLE_BYTE_BUFFER)) != -1) {
                byteArrayOutputStream.write(data, 0, count);
            }
            byteArrayOutputStream.flush();
            zin.closeEntry();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        }
        return null;
    }

    @NonNull
    private static byte[] oldDeCompress(final @NonNull byte[] compressed) throws DataFormatException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(compressed.length);
        final Inflater decompressor = new Inflater();
        try {
            decompressor.setInput(compressed);
            final byte[] buf = new byte[StreamUtil.MIDDLE_BYTE_BUFFER];
            while (!decompressor.finished()) {
                final int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            }
        } finally {
            decompressor.end();
        }
        return bos.toByteArray();
    }
}
