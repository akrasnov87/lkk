package ru.mobnius.simple_core.utils;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ru.mobnius.simple_core.BaseApp;

public class DownloadUtil {

    public static void downloadFile(final @NonNull OnCompleteCallback<File> callback,
                                    final @NonNull OnErrorCallback<String> errorCallback,
                                    final @Nullable OnProgressCallback progressCallback,
                                    final @NonNull String fileName,
                                    final @NonNull String Url,
                                    final @NonNull Handler handler,
                                    final @NonNull File downloadFolder) {
        final File file = new File(downloadFolder, fileName);
        final Executor executor = Executors.newSingleThreadExecutor();
        final Callable<Void> callable = () -> {
            final URL downloadUrl = new URL(Url);
            final HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
            connection.setConnectTimeout(BaseApp.CONNECTION_TIMEOUT);
            connection.setReadTimeout(BaseApp.CONNECTION_TIMEOUT);
            connection.connect();
            final int responseCode = connection.getResponseCode();
            if (responseCode != BaseApp.RESPONSE_OK) {
                handler.post(() -> {
                    String error = String.format(BaseApp.CONNECTION_ERROR_WITH_CODE, responseCode);
                    errorCallback.onError(error);
                });
                return null;
            }
            final int fileLength = connection.getContentLength();
            if (fileLength <= 0) {
                handler.post(() -> errorCallback.onError(BaseApp.DOWNLOAD_PROBLEM));
                return null;
            }
            final InputStream input = connection.getInputStream();
            final OutputStream output = new FileOutputStream(file);
            int progress = 0;
            final byte[] data = new byte[StreamUtil.MIDDLE_BYTE_BUFFER];
            int count;
            while ((count = input.read(data)) != StreamUtil.EOF) {
                output.write(data, 0, count);
                progress += count;
                if (progressCallback != null) {
                    final int progressPercentage = (progress * 100 / fileLength);
                    handler.post(() -> progressCallback.onProgress(progressPercentage));
                }
            }
            handler.post(() -> callback.onComplete(file));
            output.close();
            input.close();
            connection.disconnect();
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

    public static void downloadBytes(final @NonNull OnCompleteCallback<byte[]> callback,
                                     final @NonNull OnErrorCallback<String> errorCallback,
                                     final @Nullable OnProgressCallback progressCallback,
                                     final @NonNull String requestUrl, final @NonNull Handler handler) {
        final Executor executor = Executors.newSingleThreadExecutor();
        final Callable<Void> callable = () -> {
            final URL url = new URL(requestUrl);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(BaseApp.CONNECTION_TIMEOUT);
            connection.setReadTimeout(BaseApp.CONNECTION_TIMEOUT);
            connection.connect();
            final int responseCode = connection.getResponseCode();

            if (responseCode != BaseApp.RESPONSE_OK) {
                handler.post(() -> {
                    String error = String.format(BaseApp.CONNECTION_ERROR_WITH_CODE, responseCode);
                    errorCallback.onError(error);
                });
                return null;
            }
            final int contentLength = connection.getContentLength();
            if (contentLength <= 0) {
                handler.post(() -> errorCallback.onError(BaseApp.DOWNLOAD_PROBLEM));
                return null;
            }
            final InputStream inputStream = connection.getInputStream();
            final ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

            final byte[] buffer = new byte[StreamUtil.MIDDLE_BYTE_BUFFER];
            int progress = 0;
            int count;
            while ((count = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, count);
                progress += count;
                if (progressCallback != null) {
                    final int progressPercentage = (progress * 100 / contentLength);
                    handler.post(() -> progressCallback.onProgress(progressPercentage));
                }
            }
            handler.post(() -> callback.onComplete(byteBuffer.toByteArray()));
            byteBuffer.close();
            inputStream.close();
            connection.disconnect();
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
