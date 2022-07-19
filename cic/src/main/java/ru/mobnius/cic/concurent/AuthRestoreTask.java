package ru.mobnius.cic.concurent;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;

import ru.mobnius.simple_core.data.Meta;
import ru.mobnius.simple_core.utils.AuthUtil;

/**
 * Класс восстановления авторизации через e-mail
 * в дополнительном потоке
 */
public class AuthRestoreTask implements Callable<Meta> {
    @NonNull
    private final String eMailAddress;

    public AuthRestoreTask(final @NonNull String eMailAddress) {
        this.eMailAddress = eMailAddress;
    }

    @NonNull
    @Override
    public Meta call() throws Exception {
        return AuthUtil.restoreAuthInfo(eMailAddress);
    }
}
