package ru.mobnius.simple_core.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.mobnius.simple_core.utils.IntUtil;

/**
 * Класс для рабоыт с версией приложения
 * Применение:
 * Version version = new Version();
 * // Проверяем валидность версии
 * version.isValid(versionNumber);
 * // Проверяем не пустая ли версия
 * version.isEmpty(versionNumber)
 */
public class Version {
    /**
     * Дата рождения приложения
     */
    public static Date BIRTH_DAY = new GregorianCalendar(2022, 1, 21).getTime();
    /**
     * альфа версия
     */
    public static final int ALPHA = 0;
    /**
     * бета версия
     */
    public static final int BETA = 1;
    /**
     * релиз кандидан
     */
    public static final int RELEASE_CANDIDATE = 2;
    /**
     * публичный выпуск
     */
    public static final int PRODUCTION = 3;

    /**
     * Получение даты публикации
     *
     * @param birthDay дата создания
     * @param version  номер версии
     * @return дата сборки приложения
     */
    @Nullable
    public Date getBuildDate(final @NonNull Date birthDay, final @NonNull String version) {
        final int[] parts = getVersionParts(version);
        if (isNotEmpty(parts)) {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(birthDay);
            cal.add(Calendar.DAY_OF_MONTH, parts[1]);
            cal.add(Calendar.MINUTE, parts[3]);
            return cal.getTime();
        }
        return null;
    }

    /**
     * Получение статуса версии
     *
     * @param version номер версии
     * @return возращается одно из значений: ALPHA, BETA, RELEASE_CANDIDATE, PRODUCTION, либо Null
     */
    public int getVersionState(final @NonNull String version) {
        final int[] parts = getVersionParts(version);
        if (isNotEmpty(parts)) {
            return parts[2];
        }
        return -1;
    }

    /**
     * Получение частей версии
     *
     * @param version номер версии
     * @return массив чисел
     */
    @NonNull
    public int[] getVersionParts(final @NonNull String version) {
        final String[] data = version.split("\\.");
        final int[] parts = new int[4];
        if (data.length == 4) {
            try {
                final int one = IntUtil.getIntOrZero(data[0]);
                final int two = IntUtil.getIntOrZero(data[1]);
                final int three = IntUtil.getIntOrZero(data[2]);
                final int four = IntUtil.getIntOrZero(data[3]);

                parts[0] = one;
                parts[1] = two;
                parts[2] = three;
                parts[3] = four;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return parts;
    }

    private boolean isNotEmpty(final @NonNull int[] parts) {
        for (int part : parts) {
            if (part != 0) {
                return true;
            }
        }
        return false;
    }
}
