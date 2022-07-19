package ru.mobnius.simple_core.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.mobnius.simple_core.utils.StringUtil;

public class AlertDialogUtil {

    public static void alert(final @NonNull Context context,
                             final @NonNull String message,
                             final @NonNull String positiveText,
                             final @NonNull DialogInterface.OnClickListener positiveListener,
                             final @Nullable String negativeText,
                             final @Nullable DialogInterface.OnClickListener negativeListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(positiveText, positiveListener);
        if (StringUtil.isNotEmpty(negativeText)) {
            builder.setNegativeButton(negativeText, negativeListener);
        }
        builder.show();
    }

    public static void cancelableAlert(final @NonNull Context context,
                                       final @NonNull String message,
                                       final @NonNull String positiveText,
                                       final @NonNull DialogInterface.OnClickListener positiveListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(positiveText, positiveListener);
        builder.show();
    }
}
