package ru.mobnius.simple_core.data.credentials;

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Объект для basic-authorization
 */
public class BasicCredentials {
    /**
     * Логин пользователя
     */
    @NonNull
    public final String login;
    /**
     * Пароль пользователя
     */
    @NonNull
    public final String password;

    /**
     * Конструктор
     *
     * @param login    логин
     * @param password пароль
     */
    public BasicCredentials(final @NonNull String login, final @NonNull String password) {
        this.login = login;
        this.password = password;
    }

    /**
     * Токен авторизации, полученный в результате запроса
     *
     * @return токен авторизации
     */
    @NonNull
    public String getToken() {
        final String str = this.login + StringUtil.COLON + this.password;
        final byte[] bytesEncoded = Base64.encode(str.getBytes(), Base64.NO_WRAP);
        return new String(bytesEncoded);
    }

    /**
     * Метод для чтения токен
     *
     * @param token токен авторизации
     * @return параметры безопасности
     */
    @Nullable
    public static BasicCredentials decode(@NonNull String token) {
        token = token.replace("Token ", StringUtil.EMPTY);
        final String authorization = new String(Base64.decode(token, Base64.DEFAULT));
        if (authorization.contains(StringUtil.COLON)) {
            final String[] data = authorization.split(StringUtil.COLON);
            if (data.length >= 2) {
                return new BasicCredentials(data[0], data[1]);
            }
        }
        return null;
    }
}