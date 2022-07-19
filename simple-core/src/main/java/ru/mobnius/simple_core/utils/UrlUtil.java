package ru.mobnius.simple_core.utils;

import androidx.annotation.NonNull;

public class UrlUtil {
    /**
     * Получение из адресной строки имени домена
     *
     * @param url адресная строка
     * @return доменное имя с протоколом
     */
    @NonNull
    public static String getDomainUrl(final @NonNull String url) {
        int count = 0;
        final StringBuilder builder = new StringBuilder(url.length());

        for (int i = 0; i < url.length(); i++) {
            final char ch = url.charAt(i);
            if (ch == '/') {
                count++;
            }

            if (count >= 3) {
                break;
            }
            builder.append(ch);
        }
        return builder.toString();
    }

    /**
     * Получение виртуального пути из адресной строки
     *
     * @param url адресная строка
     * @return виртуальный путь
     */
    @NonNull
    public static String getPathUrl(final @NonNull String url) {
        final String domain = getDomainUrl(url);
        return url.replace(domain, StringUtil.EMPTY);
    }
}
