package ru.mobnius.simple_core.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ru.mobnius.simple_core.data.Version;

public class DateUtil {
    public static final int ONE_DAY_TIME = 24 * 60 * 60 * 1000;
    public static final String USER_FORMAT = "dd.MM.yyyy HH:mm:ss";
    public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String USER_SHORT_FORMAT = "dd.MM.yyyy";
    public static final String USER_TIME_FORMAT = "HH:mm:ss";
    public static final String METER_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Null-safe преобразование строки в дату
     *
     * @param date дата
     * @return результат преобразования или текущую дату
     */
    @NonNull
    public static Date getNonNullDateFromServerString(final @Nullable String date) {
        if (StringUtil.isEmpty(date)) {
            return new Date();
        }
        try {
            final SimpleDateFormat serverDateFormat = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
            final Date nonNullDate = serverDateFormat.parse(date);
            if (nonNullDate == null) {
                return new Date();
            }
            return new Date(nonNullDate.getTime() + TimeZone.getDefault().getOffset(nonNullDate.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * Null-safe преобразование строки в дату
     *
     * @param date дата
     * @return результат преобразования или текущую дату
     */
    @NonNull
    public static Date getNonNullMeterDateFromServerString(final @Nullable String date) {
        if (StringUtil.isEmpty(date)) {
            return new Date();
        }
        final SimpleDateFormat dateFormat = new SimpleDateFormat(METER_DATE_FORMAT, Locale.getDefault());
        try {
            final Date nonNullDate = dateFormat.parse(date);
            if (nonNullDate == null) {
                return new Date();
            }
            return nonNullDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * Nullable преобразование серверной строки в дату
     *
     * @param date строка даты в формате сервера
     * @return результат преобразования или null
     */
    @Nullable
    public static Date getNullableDateFromServerString(final @Nullable String date) {
        if (StringUtil.isEmpty(date)) {
            return null;
        }
        final SimpleDateFormat dateFormat = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Возвращение строки для отображения пользователю
     *
     * @return возврщается строка даты в пользовательском формате
     */
    @NonNull
    public static String getDateToStringForUser(final @NonNull Date date) {
        try {
            final SimpleDateFormat dateFormat = new SimpleDateFormat(USER_FORMAT, Locale.getDefault());
            return dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return StringUtil.EMPTY;
        }
    }

    @Nullable
    public static Date convertToDateFromUserString(final @NonNull String dateString) {
        if (StringUtil.isEmpty(dateString)) {
            return null;
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(USER_SHORT_FORMAT, Locale.getDefault());
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Преобразовать дату в пользовательскую строку
     *
     * @param date дата
     * @return возврщается дата в виде строки либо пустая строка если произошла ошибка
     */
    @NonNull
    public static String getNonNullDateTextMiddle(final @Nullable Date date) {
        return convertDateToCustomString(date, USER_FORMAT);
    }


    @NonNull
    public static String getNonNullDateTextShort(final @Nullable Date date) {
        return convertDateToCustomString(date, USER_SHORT_FORMAT);
    }


    @NonNull
    public static Date getNonNullSystemDate(final @Nullable String date) {
        if (StringUtil.isEmpty(date)) {
            return new Date();
        }
        final SimpleDateFormat dateFormat = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
        try {
            final Date nonNullDate = dateFormat.parse(date);
            if (nonNullDate == null) {
                return new Date();
            }
            return nonNullDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * Возвращение даты в c GMT временной зоной
     *
     * @return возврщается даты или пустая строка если произошла ошибка парсинга
     */
    @NonNull
    public static String getNewDateStringForServer() {
        try {
            final DateFormat dateFormatGmt = SimpleDateFormat.getDateTimeInstance();
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            final DateFormat dateFormatLocal = SimpleDateFormat.getDateTimeInstance();
            Date currentDate = dateFormatLocal.parse(dateFormatGmt.format(new Date()));
            final SimpleDateFormat dateFormat = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
            if (currentDate == null) {
                currentDate = new Date();
            }
            return dateFormat.format(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
            return StringUtil.EMPTY;
        }
    }

    /**
     * Возвращение даты в c GMT временной зоной
     *
     * @return возврщается даты или пустая строка если произошла ошибка парсинга
     */
    @NonNull
    public static String getDateStringForServer(@Nullable Date date) {
        if (date == null) {
            date = new Date();
        }
        try {
            final DateFormat dateFormatGmt = SimpleDateFormat.getDateTimeInstance();
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            final DateFormat dateFormatLocal = SimpleDateFormat.getDateTimeInstance();
            Date currentDate = dateFormatLocal.parse(dateFormatGmt.format(date));
            final SimpleDateFormat dateFormat = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
            if (currentDate == null) {
                currentDate = new Date();
            }
            return dateFormat.format(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
            return StringUtil.EMPTY;
        }
    }

    /**
     * Возвращение строки для отображения пользователю
     *
     * @return возврщается строка даты в пользовательском формате
     */
    @NonNull
    public static String getDateStringForUser() {
        try {
            final SimpleDateFormat dateFormat = new SimpleDateFormat(USER_FORMAT, Locale.getDefault());
            return dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return StringUtil.EMPTY;
        }
    }

    /**
     * Null-safe преобразование в строку с определнным форматом
     *
     * @param date   дата
     * @param format формат даты
     * @return возврщается строка
     */
    @NonNull
    public static String convertDateToCustomString(final @Nullable Date date, final @Nullable String format) {
        if (date == null) {
            return StringUtil.EMPTY;
        }
        final SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        try {
            return dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return StringUtil.EMPTY;
        }
    }

    /**
     * Генерация TID
     *
     * @return уникальный идентификатор
     */
    public static int geenerateTid() {
        return Math.abs((int) ((new Date().getTime() - Version.BIRTH_DAY.getTime())));
    }

    /**
     * Null-safe преобразование строки в дату
     *
     * @param date дата
     * @return результат преобразования или текущую дату
     */
    @NonNull
    public static String getNonNullShortDateText(final @Nullable String date) {
        final Date nonNullDate = getNonNullDateFromServerString(date);
        return getNonNullDateTextShort(nonNullDate);
    }


    @NonNull
    public static String dateToTimeString(final @Nullable Date date) {
        return convertDateToCustomString(date, USER_TIME_FORMAT);
    }

    public static boolean isNotMatchesUserShortFormat(@Nullable String date) {
        if (date == null) {
            return true;
        }
        return !date.matches("\\d{2}\\.\\d{2}\\.\\d{4}");
    }

    public static boolean isNotMatchesYearFormat(@Nullable String date) {
        if (date == null) {
            return true;
        }
        return !date.matches("\\d{4}");
    }
}
