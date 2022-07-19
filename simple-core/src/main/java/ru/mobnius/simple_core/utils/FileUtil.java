package ru.mobnius.simple_core.utils;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOExceptionList;
import org.apache.commons.io.file.Counters;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.io.file.StandardDeleteOption;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FileUtil {
    public static final String PICTURES_SUBFOLDER = "PICTURES";
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int EOF = -1;

    @Nullable
    public static File getFileForCamera(final @NonNull Context context) throws IOException {
        final File dir = context.getFilesDir();
        if (!dir.exists()) {
            if (forceMkdir(dir) == null) {
                return null;
            }
        }
        final File subDir = new File(dir, FileUtil.PICTURES_SUBFOLDER);
        if (!subDir.exists()) {
            if (forceMkdir(subDir) == null) {
                return null;
            }
        }
        final String pictureName = UUID.randomUUID() + BitmapUtil.IMAGE_TYPE;
        final File file = new File(subDir, pictureName);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }

    /**
     * Метод который создает субдиректорию с именем {@param subfolderName} в директории {@param privateAppFolder}
     *
     * @param privateAppFolder - директория - подразумевается что это привчатная директория
     *                         приложения полученная путем вызова метода context.getFilesDir()
     * @param subfolderName    - имя поддериктории
     * @return объект File - поддиректорию в директории {@param privateAppFolder}
     * @throws IOException - проброс исключения при попытке создания файла
     */
    @NonNull
    public static File getAppSubfolder(final @Nullable File privateAppFolder, final @NonNull String subfolderName) throws IOException {

        if (privateAppFolder == null || !privateAppFolder.exists()) {
            FileUtil.forceMkdir(privateAppFolder);
        }
        final File subdir = new File(privateAppFolder, subfolderName);
        if (!subdir.exists()) {
            FileUtil.forceMkdir(subdir);
        }
        return subdir;
    }

    @Nullable
    public static File forceMkdir(final @Nullable File directory) throws IOException {
        if (directory == null) {
            return null;
        }
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                return null;
            }
        } else {
            if (!directory.mkdirs()) {
                if (!directory.isDirectory()) {
                    return null;
                }
            }
        }
        return directory;
    }

    private static void mkdirs(final @Nullable File directory) throws IOException {
        if ((directory != null) && (!directory.mkdirs() && !directory.isDirectory())) {
            throw new IOException("Невозможно создать директорию '" + directory + "'.");
        }
    }

    public static void deleteQuietly(final @Nullable File file) {
        if (file == null) {
            return;
        }
        try {
            if (file.isDirectory()) {
                cleanDirectory(file);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        try {
            if (!file.delete()){
                forceDelete(file);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static void cleanDirectory(final @NonNull File directory) throws IOException {
        final File[] files = listFiles(directory);

        final List<Exception> causeList = new ArrayList<>();
        for (final File file : files) {
            try {
                forceDelete(file);
            } catch (final IOException ioe) {
                causeList.add(ioe);
            }
        }

        if (!causeList.isEmpty()) {
            throw new IOExceptionList(directory.toString(), causeList);
        }
    }

    public static void forceDelete(final @NonNull File file) throws IOException {
        final Counters.PathCounters deleteCounters;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                deleteCounters = PathUtils.delete(file.toPath(), PathUtils.EMPTY_LINK_OPTION_ARRAY,
                        StandardDeleteOption.OVERRIDE_READ_ONLY);
            } else {
                if (!file.delete()) {
                    return;
                }
                return;
            }
        } catch (final IOException e) {
            throw new IOException("Невозможно удалить файл: " + file, e);
        }

        if (deleteCounters.getFileCounter().get() < 1 && deleteCounters.getDirectoryCounter().get() < 1) {
            throw new FileNotFoundException("Файл не существует: " + file);
        }
    }

    @NonNull
    private static File[] listFiles(final @NonNull File directory) throws IOException {
        requireDirectoryExists(directory);
        final File[] files = directory.listFiles();
        if (files == null) {
            throw new IOException("Неизвестная ошибка ввода-вывода со списком содержимого директории: " + directory);
        }
        return files;
    }

    private static void requireDirectoryExists(final @NonNull File directory) {
        requireExists(directory);
        requireDirectory(directory);
    }

    private static void requireExists(final @NonNull File file) {
        Objects.requireNonNull(file);
        if (!file.exists()) {
            throw new IllegalArgumentException(
                    "Элемент файловой системы для параметра 'директория' не существует: '" + file + "'");
        }
    }

    private static void requireDirectory(final @NonNull File directory) {
        Objects.requireNonNull(directory);
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Параметр не является директорией: '" + directory + "'");
        }
    }

    @NonNull
    public static byte[] getBytesFromFile(final @NonNull File file) {
        if (file.exists()) {
            try (FileInputStream inputStream = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(inputStream);
                 ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
                byte[] data = new byte[DEFAULT_BUFFER_SIZE];
                int count;
                while ((count = bis.read(data, 0, DEFAULT_BUFFER_SIZE)) != EOF) {
                    buf.write(data, 0, count);
                }
                buf.flush();
                return buf.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }


}