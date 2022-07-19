package ru.mobnius.simple_core.data.credentials;

import androidx.annotation.NonNull;

import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Объект пользователя, авторизовавшегося в приложении
 */
public class BasicUser {

    /**
     * список ролей у пользователя. Разделителем является точка
     */
    @NonNull
    public final String claims;
    @NonNull
    private final BasicCredentials credentials;
    private final long userId;
    @NonNull
    private final String[] roles;

    public BasicUser(final @NonNull BasicCredentials credentials, final long userId, final @NonNull String claims) {
        this.credentials = credentials;
        this.userId = userId;
        final String trimClaims = claims.replaceAll("^.", StringUtil.EMPTY).replaceAll(".$", StringUtil.EMPTY);
        if (trimClaims.contains(StringUtil.DOT)) {
            roles = trimClaims.split("\\.");
        } else {
            roles = new String[1];
            roles[0] = trimClaims;
        }
        this.claims = claims;
    }

    public boolean userInRole(final @NonNull String roleName) {
        for (final String s : roles) {
            if (roleName.equals(s))
                return true;
        }

        return false;
    }

    public long getUserId() {
        return userId;
    }

    /**
     * Возращается объект с данным для авторизации
     *
     * @return данные об авторизации
     */
    public @NonNull
    BasicCredentials getCredentials() {
        return credentials;
    }
}

