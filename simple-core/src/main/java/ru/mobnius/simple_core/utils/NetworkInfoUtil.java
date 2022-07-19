package ru.mobnius.simple_core.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NetworkInfoUtil {
    /**
     * Проверка на подключение к сети
     * Метод плох тем, что вернет true если есть подключение к сети
     * wifi без выхода в интернет, но хорош тем что может выполняться
     * в главном потоке
     *
     * @param context контекст
     * @return true - подключено к сети
     */
    public static boolean isNetworkAvailable(final @NonNull Context context) {
        final ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Проверка на подключение к сети интернет
     * Метод плох тем, что выполняется не в главном потоке, а так же может вернуть false
     * в случае если подключение по 8.8.8.8 запрщено в данной сети, но
     * хорош тем, что выполняется очень быстро и если возвращает true,
     * то есть реальное подключение к сети интернет
     *
     * @param completeCallback обратный вызов
     */
    public static void checkRealConnection(final @NonNull OnCompleteCallback<Boolean> completeCallback) {
        final Executor executor = Executors.newSingleThreadExecutor();
        final Callable<Boolean> callable = () -> {
            try {
                final Socket sock = new Socket();
                sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
                sock.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        };

        executor.execute(() -> {
            Boolean isConnected = null;
            try {
                isConnected = callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isConnected == null) {
                    isConnected = false;
                }
                completeCallback.onComplete(isConnected);
        });
    }
}
