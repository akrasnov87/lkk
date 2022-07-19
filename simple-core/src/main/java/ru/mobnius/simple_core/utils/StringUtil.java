package ru.mobnius.simple_core.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class StringUtil {
    @NonNull
    public static final String DOT = ".";
    @NonNull
    public static final String COMMA = ",";
    @NonNull
    private static final String NULL = "null";
    @NonNull
    public static final String SPACE = " ";
    @NonNull
    public static final String COLON = ":";
    @NonNull
    public static final String EMPTY = "";
    @NonNull
    public static final String ERROR = "error";

    /**
     * Null-safe проверка строки на пустоту
     *
     * @param cs строка для проверки
     * @return true если строка пустая или равна null
     */
    public static boolean isEmpty(final @Nullable CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * Null-safe проверка строки на непустоту
     *
     * @param cs строка для проверки
     * @return true если строка не равна null и не пустая
     */
    public static boolean isNotEmpty(final @Nullable CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * Null-safe проверка строк на пустоту
     *
     * @param cs строки для проверки
     * @return true если хотя бы одна строка равна null или пустая
     */
    public static boolean isAtLeastOneEmpty(final @Nullable CharSequence... cs) {
        if (cs == null) {
            return true;
        }
        for (final CharSequence charSequence : cs) {
            if (isEmpty(charSequence)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Null-safe преобразование строки. Делает строку заглавной, изменяя регистр первого символа
     *
     * @param str строка для преобразования
     * @return Преобразованную строку или пустую строку если {@param str} равен null
     */
    @NonNull
    public static String capitalize(final @Nullable String str) {
        final int strLen = length(str);
        if (strLen == 0 || str == null) {
            return EMPTY;
        }

        final int firstCodepoint = str.codePointAt(0);
        final int newCodePoint = Character.toTitleCase(firstCodepoint);
        if (firstCodepoint == newCodePoint) {
            return str;
        }

        final int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        newCodePoints[outOffset++] = newCodePoint;
        for (int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; ) {
            final int codepoint = str.codePointAt(inOffset);
            newCodePoints[outOffset++] = codepoint;
            inOffset += Character.charCount(codepoint);
        }
        return new String(newCodePoints, 0, outOffset);
    }

    /**
     * Null-safe проверка на содержание в одной строке другой строки. Порядок параметров имеет значение.
     *
     * @param seq       строка в которой ищем
     * @param searchSeq строка которую ищем
     * @return false если один из параметров null или если {@param searchSeq} не содержится в {@param seq}
     */
    public static boolean contains(final @Nullable CharSequence seq, final @Nullable CharSequence searchSeq) {
        if (seq == null || searchSeq == null) {
            return false;
        }

        return indexOf(seq, searchSeq) >= 0;
    }

    /**
     * Null-safe регистронезависимая проверка на содержание в одной строке другой строки. Порядок параметров имеет значение.
     *
     * @param str       строка в которой ищем
     * @param searchStr строка которую ищем
     * @return false если один из параметров null или если {@param searchStr} не содержится в {@param str}
     */
    public static boolean containsIgnoreCase(final @Nullable CharSequence str, final @Nullable CharSequence searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        final int len = searchStr.length();
        final int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (regionMatches(str, i, searchStr, len)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Null-safe регистронезависимая проверка на не содержание в одной строке другой строки. Порядок параметров имеет значение.
     *
     * @param str       строка в которой ищем
     * @param searchStr строка которую ищем
     * @return false если один из параметров null или если {@param searchStr} не содержится в {@param str}
     */
    public static boolean containsNotIgnoreCase(final @Nullable CharSequence str, final @Nullable CharSequence searchStr) {
        return !containsIgnoreCase(str, searchStr);
    }

    /**
     * Null-safe обработка строки на пустоту
     *
     * @param str строка для проверки
     * @return пустую строку если строка равна null или изначальную строку
     */
    @NonNull
    public static String defaultEmptyString(final @Nullable CharSequence str) {
        return str == null ? EMPTY : str.toString();
    }

    /**
     * Null-safe удаление из строки пробелов
     *
     * @param str строка для проверки
     * @return пустую строку если строка равна null или строку с удаленными из нее пробелами
     */
    @NonNull
    public static String deleteWhitespace(final @Nullable String str) {
        if (isEmpty(str)) {
            return EMPTY;
        }
        final int sz = str.length();
        final char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count++] = str.charAt(i);
            }
        }
        if (count == sz) {
            return str;
        }
        if (count == 0) {
            return EMPTY;
        }
        return new String(chs, 0, count);
    }

    public static boolean notEquals(final @Nullable CharSequence cs1, final @Nullable CharSequence cs2){
        return !equals(cs1, cs2);
    }

    public static boolean notEqualsIgnoreCase(final @Nullable CharSequence cs1, final @Nullable CharSequence cs2){
        return !equalsIgnoreCase(cs1, cs2);
    }
    /**
     * Null-safe регистрозависимая проверка на равенство двух строк
     *
     * @param cs1 первая строка
     * @param cs2 вторая строка
     * @return false если один из параметров null или если {@param cs1} не равен {@param cs2}
     */
    public static boolean equals(final @Nullable CharSequence cs1, final @Nullable CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1.length() != cs2.length()) {
            return false;
        }
        if (cs1 instanceof String && cs2 instanceof String) {
            return cs1.equals(cs2);
        }
        final int length = cs1.length();
        for (int i = 0; i < length; i++) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Null-safe регистронезависимая проверка на равенство двух строк
     *
     * @param cs1 первая строка
     * @param cs2 вторая строка
     * @return false если один из параметров null или если {@param cs1} не равен {@param cs2}
     */
    public static boolean equalsIgnoreCase(final @Nullable CharSequence cs1, final @Nullable CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1.length() != cs2.length()) {
            return false;
        }
        return regionMatches(cs1, 0, cs2, cs1.length());
    }

    /**
     * Null-safe получение массива байтов из строки
     *
     * @param string входящая строка
     * @return пустой массив байтов если строка равна null или строку кодированную в массив байтов (кодировка UTF-8)
     */
    @NonNull
    public static byte[] getBytes(final @Nullable String string) {
        return string == null ? new byte[0] : string.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * Null-safe проверка что строка содержит только числа
     *
     * @param charSequence входящая строка
     * @return false если строка равна null или хотя бы один символ не является числом
     */
    public static boolean isAllNumeric(final @Nullable CharSequence charSequence) {
        if (isEmpty(charSequence)) {
            return false;
        }
        final int sz = charSequence.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(charSequence.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Null-safe проверка что строка является целым числом или  числом с плавающей точкой
     *
     * @param charSequence входящая строка
     * @return false если строка равна null или хотя бы один символ помимо точки или запятой не является числом
     */
    public static boolean isDouble(final @Nullable CharSequence charSequence) {
        if (isEmpty(charSequence)) {
            return false;
        }
        String number = charSequence.toString();
        number = number.replaceAll("[.,]", EMPTY);
        return isAllNumeric(number);
    }

    /**
     * Получение индекса строки в другой строке
     *
     * @param cs         входящая строка
     * @param searchChar строка для поиска
     * @return индекс начала строки в другой строке или -1
     */
    private static int indexOf(final @NonNull CharSequence cs, final @NonNull CharSequence searchChar) {
        if (cs instanceof String) {
            return ((String) cs).indexOf(searchChar.toString());
        } else if (cs instanceof StringBuilder) {
            return ((StringBuilder) cs).indexOf(searchChar.toString(), 0);
        } else if (cs instanceof StringBuffer) {
            return ((StringBuffer) cs).indexOf(searchChar.toString(), 0);
        }
        return cs.toString().indexOf(searchChar.toString());
    }

    /**
     * Null-safe получение длины строки
     *
     * @param cs входящая строка
     * @return 0 если строка равна null или длину строки
     */
    private static int length(final @Nullable CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    /**
     * Регистронезависимое сравнение строки и подстроки
     * @param cs строка в которой происходит поиск и сравнение подстроки
     * @param thisStart начальное смещение в строке {@param cs}
     * @param substring строка поиск которой происходит
     * @param length количество символов для сравнения
     * @return true если {@param substring} содержится в строке {@param cs} начиная с {@param thisStart}
     */
    private static boolean regionMatches(final @NonNull CharSequence cs, final int thisStart,
                                         final @NonNull CharSequence substring, final int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(true, thisStart, (String) substring, 0, length);
        }
        int index1 = thisStart;
        int index2 = 0;
        int tmpLen = length;

        final int srcLen = cs.length() - thisStart;
        final int otherLen = substring.length();

        if (thisStart < 0 || length < 0) {
            return false;
        }

        if (srcLen < length || otherLen < length) {
            return false;
        }

        while (tmpLen-- > 0) {
            final char c1 = cs.charAt(index1++);
            final char c2 = substring.charAt(index2++);

            if (c1 == c2) {
                continue;
            }

            final char u1 = Character.toUpperCase(c1);
            final char u2 = Character.toUpperCase(c2);
            if (u1 != u2 && Character.toLowerCase(u1) != Character.toLowerCase(u2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Сокращение guid
     *
     * @param guid UUID
     * @return возвращается до символа -
     */
    @NonNull
    public static String getShortGuid(final @NonNull String guid) {
        if (isNotEmpty(guid)) {
            return guid.substring(0, guid.indexOf("-"));
        } else {
            return guid;
        }
    }


    /**
     * Преобразование байтов в КБ, МБ, ГБ
     *
     * @param size размер
     * @return строка
     */
    @NonNull
    public static String getSize(final long size) {
        String s;
        final double kb = (double) size / 1024;
        final double mb = kb / 1024;
        final double gb = mb / 1024;
        final double tb = gb / 1024;
        if (size < 1024) {
            s = size + " байт";
        } else if (size < 1024 * 1024) {
            s = String.format(Locale.getDefault(), "%.2f", kb) + " КБ";
        } else if (size < 1024 * 1024 * 1024) {
            s = String.format(Locale.getDefault(), "%.2f", mb) + " МБ";
        } else if (size < (long) 1024 * (long) 1024 * (long) 1024 * (long) 1024) {
            s = String.format(Locale.getDefault(), "%.2f", gb) + " ГБ";
        } else {
            s = String.format(Locale.getDefault(), "%.2f", tb) + " ТБ";
        }
        return s;
    }

    /**
     * Заполение разделителями
     *
     * @param count     количество
     * @param separator разделитель
     * @return возвращается строка
     */
    @NonNull
    public static String fullSpace(final int count, final @Nullable String separator) {
        if (count > 0 && isNotEmpty(separator)) {
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < count; i++) {
                builder.append(separator);
            }
            return builder.toString();
        } else {
            return EMPTY;
        }
    }

    /**
     * Получение md5-хеш кода
     *
     * @param inputString входная строка
     * @return хеш-код
     */
    @Nullable
    public static String md5(String inputString) {
        final String MD5 = "MD5";
        try {
           final MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(inputString.getBytes());
            final byte[] messageDigest = digest.digest();

            final StringBuilder hexString = new StringBuilder();
            for (final byte aMessageDigest : messageDigest) {
                final StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * Получение mime по имени файла
     *
     * @param name имя файла
     * @return MIME-тип
     */
    @NonNull
    public static String getMimeByName(String name) {
        final String extension = defaultEmptyString(getExtension(name));
        switch (extension) {
            case ".jpg":
                return "image/jpeg";

            case ".png":
                return "image/png";

            case ".webp":
                return "image/webp";

            case ".mp3":
                return "audio/mpeg";

            case ".mp4":
                return "video/mp4";

            default:
                return "application/octet-stream";
        }
    }

    /**
     * Получение расширения файла
     *
     * @param name имя файла
     * @return расширение
     */
    @Nullable
    public static String getExtension(String name) {
        if (name != null && !name.isEmpty()) {
            int strLength = name.lastIndexOf(DOT);
            if (strLength >= 0) {
                final String ext = name.substring(strLength + 1).toLowerCase();
                if (ext.isEmpty()) {
                    return null;
                } else {
                    return DOT + ext;
                }
            }
        }
        return null;
    }

    /**
     * Преобразование исключения в строку
     *
     * @param e исключение
     * @return строка
     */
    @NonNull
    public static String exceptionToString(final @NonNull Throwable e) {
        final Writer writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
