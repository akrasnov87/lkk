package ru.mobnius.cic.ui.verification;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.component.CicEditText;
import ru.mobnius.simple_core.utils.DateUtil;
import ru.mobnius.simple_core.utils.StringUtil;

public class CicEditValidationUtil {

    public static boolean validateFutureDate(final @Nullable String date, final @NonNull CicEditText editText, final boolean isError) {
        final Context context = editText.getContext();
        if (StringUtil.isEmpty(date)) {
            resolveMessageType(editText, isError, context.getString(R.string.must_to_be_not_empty));
            return false;
        }
        if (DateUtil.isNotMatchesUserShortFormat(date)) {
            resolveMessageType(editText, isError, context.getString(R.string.date_must_be_this_format));
            return false;
        }
        final Date d = DateUtil.convertToDateFromUserString(date);
        if (d == null) {
            resolveMessageType(editText, isError, context.getString(R.string.date_must_be_this_format));
            return false;
        }
        if (d.after(new Date()) || isSameDay(d, new Date())) {
            resolveMessageType(editText, isError, null);
            return true;
        } else {
            resolveMessageType(editText, isError, context.getString(R.string.date_must_be_more_than_current));
            return false;
        }

    }

    public static boolean validateDate(final @Nullable String date, final @NonNull CicEditText editText, final boolean isError) {
        final Context context = editText.getContext();
        if (StringUtil.isEmpty(date)) {
            resolveMessageType(editText, isError, context.getString(R.string.must_to_be_not_empty));
            return false;
        }
        if (DateUtil.isNotMatchesUserShortFormat(date)) {
            resolveMessageType(editText, isError, context.getString(R.string.date_must_be_this_format));
            return false;
        }
        final Date d = DateUtil.convertToDateFromUserString(date);
        if (d == null) {
            resolveMessageType(editText, isError, context.getString(R.string.date_must_be_this_format));
            return false;
        }
        resolveMessageType(editText, isError, null);
        return true;
    }

    public static boolean validatePastDate(final @Nullable String date, final @NonNull CicEditText editText, final boolean isError) {
        final Context context = editText.getContext();
        if (StringUtil.isEmpty(date)) {
            resolveMessageType(editText, isError, context.getString(R.string.must_to_be_not_empty));
            return false;
        }
        if (DateUtil.isNotMatchesUserShortFormat(date)) {
            resolveMessageType(editText, isError, context.getString(R.string.date_must_be_this_format));
            return false;
        }
        final Date d = DateUtil.convertToDateFromUserString(date);
        if (d == null) {
            resolveMessageType(editText, isError, context.getString(R.string.date_must_be_this_format));
            return false;
        }
        if (d.before(new Date()) || isSameDay(d, new Date())) {
            resolveMessageType(editText, isError, null);
            return true;
        } else {
            resolveMessageType(editText, isError, context.getString(R.string.date_must_be_less_than_current));
            return false;
        }
    }

    private static void resolveMessageType(final @NonNull CicEditText editText, final boolean isError, final @Nullable String message) {
        if (isError) {
            editText.setError(message);
        } else {
            editText.setHelperText(message);
        }
    }

    private static boolean isSameDay(final @NonNull Date first, final @NonNull Date second) {
        final Calendar cal1 = Calendar.getInstance();
        final Calendar cal2 = Calendar.getInstance();
        cal1.setTime(first);
        cal2.setTime(second);
        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public static boolean validateYear(final @Nullable String year, final @NonNull CicEditText cetYear, final boolean isError) {
        final Context context = cetYear.getContext();
        if (StringUtil.isEmpty(year)) {
            resolveMessageType(cetYear, isError, context.getString(R.string.must_to_be_not_empty));
            return false;
        }
        if (DateUtil.isNotMatchesYearFormat(year)) {
            resolveMessageType(cetYear, isError, context.getString(R.string.year_must_be_this_format));
            return false;
        }
        resolveMessageType(cetYear, isError, null);
        return true;

    }

    public static boolean validateIsFutureYear(final @Nullable String year, final @NonNull CicEditText cetYear, final boolean isError) {
        final Context context = cetYear.getContext();
        if (StringUtil.isEmpty(year)) {
            resolveMessageType(cetYear, isError, context.getString(R.string.must_to_be_not_empty));
            return false;
        }
        if (DateUtil.isNotMatchesYearFormat(year)) {
            resolveMessageType(cetYear, isError, context.getString(R.string.year_must_be_this_format));
            return false;
        }
        try {
            int y = Integer.parseInt(year);
            if (y >= Calendar.getInstance().get(Calendar.YEAR)) {
                resolveMessageType(cetYear, isError, null);
                return true;
            } else {
                resolveMessageType(cetYear, isError, context.getString(R.string.date_must_be_more_than_current));
                return false;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            resolveMessageType(cetYear, isError, context.getString(R.string.wrong_date_format));
            return false;
        }
    }

    public static boolean validateIsPastYear(final @Nullable String year, final @NonNull CicEditText cetYear, final boolean isError) {
        final Context context = cetYear.getContext();
        if (StringUtil.isEmpty(year)) {
            resolveMessageType(cetYear, isError, context.getString(R.string.must_to_be_not_empty));
            return false;
        }
        if (DateUtil.isNotMatchesYearFormat(year)) {
            resolveMessageType(cetYear, isError, context.getString(R.string.year_must_be_this_format));
            return false;
        }
        try {
            int y = Integer.parseInt(year);
            if (y <= Calendar.getInstance().get(Calendar.YEAR)) {
                resolveMessageType(cetYear, isError, null);
                return true;
            } else {
                resolveMessageType(cetYear, isError, context.getString(R.string.date_must_be_less_than_current));
                return false;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            resolveMessageType(cetYear, isError, context.getString(R.string.wrong_date_format));
            return false;
        }
    }

}
