package ru.mobnius.simple_core.data.authorization;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.mobnius.simple_core.data.Meta;
import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.utils.LongUtil;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Класс расширения мета информации для авторизации
 */
public class AuthorizationMeta extends Meta {
    /**
     * токен авторизации
     */
    @Nullable
    private final String token;
    /**
     * список ролей
     */
    @Nullable
    private final String claims;
    /**
     * идентификатор пользователя
     */
    @Nullable
    private final Long userId;

    public AuthorizationMeta(final int status,
                             final @NonNull String message,
                             final @Nullable String token,
                             final @Nullable String claims,
                             final @Nullable Long userId) {
        super(status, message);
        this.token = token;
        this.claims = claims;
        this.userId = userId;
    }

    public AuthorizationMeta(final int status, final @NonNull String message) {
        this(status, message, null, null, null);
    }

    @NonNull
    public String getToken() {
        if (StringUtil.isEmpty(token)) {
            return BaseApp.ERROR_ENG + StringUtil.COLON + BaseApp.ERROR_ENG;
        }
        return token;
    }

    @NonNull
    public String getClaims() {
        if (StringUtil.isEmpty(claims)) {
            return BaseApp.ERROR_ENG;
        }
        return claims;
    }

    public long getUserId() {
        return LongUtil.getLongOrMinus(userId);
    }
}
