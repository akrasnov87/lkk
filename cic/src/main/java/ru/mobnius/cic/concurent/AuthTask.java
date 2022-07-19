package ru.mobnius.cic.concurent;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.simple_core.data.Meta;
import ru.mobnius.simple_core.data.authorization.AuthorizationMeta;
import ru.mobnius.simple_core.data.credentials.BasicCredentials;
import ru.mobnius.simple_core.utils.AuthUtil;

/**
 * Класс для выполнения авторизации
 * в дополнительном потоке
 */
public class AuthTask implements Callable<AuthorizationMeta> {
    @NonNull
    private final String login;
    @NonNull
    private final String password;
    @NonNull
    private final String versionName;

    public AuthTask(final @NonNull String login, final @NonNull String password, final @NonNull String versionName) {
        this.login = login.trim();
        this.password = password.trim();
        this.versionName = versionName;
    }

    @NonNull
    @Override
    public AuthorizationMeta call() throws Exception {
        final BasicCredentials mCredentials = new BasicCredentials(login, password);
        try {
            return AuthUtil.requestAuth(versionName, mCredentials.login, mCredentials.password);
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthorizationMeta(Meta.ERROR_SERVER, MobniusApplication.AUTH_ERROR);
        }
    }
}
