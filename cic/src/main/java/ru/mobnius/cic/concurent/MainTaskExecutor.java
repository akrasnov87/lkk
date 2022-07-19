package ru.mobnius.cic.concurent;


import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Класс для выполнения задач в дополнительном потоке
 */
public class MainTaskExecutor {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    public final AtomicBoolean isRunning = new AtomicBoolean(false);

    public <R> void executeAsync(@NonNull final Callable<R> callable, @NonNull final MainTaskExecutorCallback<R> callback) {
        executor.execute(() -> {
            isRunning.set(true);
            final R result;
            try {
                result = callable.call();
                handler.post(() -> {
                    if (isRunning.get()) {
                        callback.onCallableComplete(result);
                    }
                    isRunning.set(false);
                });
            } catch (Exception e) {
                e.printStackTrace();
                isRunning.set(false);
                String message = e.getMessage();
                if (StringUtil.isEmpty(message)) {
                    message = StringUtil.EMPTY;
                }
                callback.onCallableError(message);
            }
        });
    }


}