package ru.mobnius.simple_core.utils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.simple_core.data.authorization.Claims;

/**
 * Служебный класс для работы с ролями
 */
public class ClaimsUtil {
    @NonNull
    private final Claims[] claims;

    public ClaimsUtil(final @NonNull String claims) {
        final String[] data = claims.split("\\.");

        final List<Claims> list = new ArrayList<>();

        for (final String item : data) {
            if (!item.isEmpty()) {
                list.add(new Claims(item));
            }
        }
        this.claims = list.toArray(new Claims[0]);
    }

    /**
     * Проверка на доступность роли
     *
     * @param claimName имя роли
     * @return результат
     */
    public boolean isExists(final @NonNull String claimName) {
        for (final Claims item : claims) {
            if (claimName.equals(item.name)) {
                return true;
            }
        }
        return false;
    }
}
