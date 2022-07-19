package ru.mobnius.simple_core.utils;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ru.mobnius.simple_core.data.GlobalSettings;
import ru.mobnius.simple_core.BaseApp;

public class UpdateUtil {
    public static final String UPDATE_DIRECTORY = "UPDATE_DIRECTORY";
    public static final String APK_START_NAME = "mobnius-";
    private static final String APK_EXTENSION = ".apk";
    private static final String UPDATE_URL = "/upload/version-file";

    /**
     * Метод для запуска потока скачивания установочного apk файла
     *
     * @param packageName      имя пакета текущего приложения
     * @param folder           приватная директория приложения
     * @param completeCallback обратный вызов завершения скачивания
     * @param errorCallback    обратный вызов ошибки
     * @param progressCallback обратный вызов прогресса
     */
    public static void getApk(final @NonNull String packageName,
                              final @Nullable File folder, final @NonNull OnCompleteCallback<String> completeCallback,
                              final @NonNull OnErrorCallback<String> errorCallback, final @NonNull OnProgressCallback progressCallback) {
        final String url = GlobalSettings.getConnectUrl() + UPDATE_URL;
        final @NonNull Handler handler = new Handler(Looper.getMainLooper());
        final String[] packageNameParts = packageName.split("\\.");
        String subApkName = BaseApp.UNKNOWN_ENG;
        if (packageNameParts.length > 0) {
            subApkName = packageNameParts[packageNameParts.length - 1];
        }
        final String apkName = APK_START_NAME + subApkName + APK_EXTENSION;
        final File file;
        try {
            file = new File(FileUtil.getAppSubfolder(folder, UPDATE_DIRECTORY), apkName);
            if (file.exists()) {
                FileUtil.deleteQuietly(file);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return;
        }
        final Executor executor = Executors.newSingleThreadExecutor();
        final Callable<Void> callable = () -> {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL sUrl = new URL(url);
                connection = (HttpURLConnection) sUrl.openConnection();
                connection.connect();
                int fileLength = connection.getContentLength();
                input = connection.getInputStream();
                output = new FileOutputStream(file);
                int progress = 0;
                final byte[] data = new byte[StreamUtil.MIDDLE_BYTE_BUFFER];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                    progress += count;
                    if (fileLength > 0) {
                        final int sendProgress = (progress * 100 / fileLength);
                        handler.post(() -> progressCallback.onProgress(sendProgress));
                    }
                }
                handler.post(() -> completeCallback.onComplete(apkName));
            } catch (Exception e) {
                errorCallback.onError(StringUtil.defaultEmptyString(e.getMessage()));
                e.printStackTrace();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        };
        executor.execute(() -> {
            try {
                callable.call();
            } catch (Exception e) {
                errorCallback.onError(StringUtil.defaultEmptyString(e.getMessage()));
                e.printStackTrace();
            }
        });
    }

}
