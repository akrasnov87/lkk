package ru.mobnius.cic.ui.uiutils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import ru.mobnius.cic.R;
import ru.mobnius.simple_core.BaseApp;

public class NotificationUtil {
    public static String LOCATION_SERVICE_NOTIFICATION_CHANNEL_ID = "ru.mobnius.simple_core.utils.LOCATION_SERVICE_CHANNEL_ID";
    public static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "ru.mobnius.simple_core.utils.DEFAULT_NOTIFICATION_CHANNEL_ID";

    public static int LOCATION_SERVICE_NOTIFICATION_ID = 2314;
    public static int DEFAULT_NOTIFICATION_ID = 1411;

    /**
     * Метод для создания канала для уведомления службы определения местоположения
     *
     * @param context контекст
     */
    public static void createLocationServiceNotificationChannel(final @NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationChannel channel = new NotificationChannel(LOCATION_SERVICE_NOTIFICATION_CHANNEL_ID,
                    BaseApp.LOCATION_SERVICE_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(BaseApp.LOCATION_SERVICE_NOTIFICATION_CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.shouldVibrate();
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }
    }

    /**
     * Метод для создания канала для обычных уведомлений
     *
     * @param context контекст
     */
    public static void createDefaultNotificationChannel(final @NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationChannel channel = new NotificationChannel(DEFAULT_NOTIFICATION_CHANNEL_ID,
                    BaseApp.DEFAULT_NOTIFICATIONS_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(BaseApp.DEFAULT_NOTIFICATION_CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.shouldVibrate();
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }
    }


    /**
     * Метод который создает объект уведомления для отображения
     * информции о запущейной службе определения геолокации
     *
     * @param context контекст
     * @param title   заголовок уведомления (название приложения)
     * @param icon    идентификатор ресурса иконки уведомления
     * @param intent  интент для открытия активити по нажатию на уведомление
     * @return объект {@link Notification} для передачи в метод запуска сервиса
     */
    //TODO: Можно добавить метод для проверки не отключил ли пользователь уведомления https://startandroid.ru/ru/uroki/vse-uroki-spiskom/515-urok-190-notifications-kanaly.html
    @NonNull
    public static Notification getNotificationForLocationService(final @NonNull Context context, final @NonNull String title,
                                                                 final @DrawableRes int icon, final @NonNull Intent intent) {
        final int piFlag;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            piFlag = PendingIntent.FLAG_IMMUTABLE;
        } else {
            piFlag = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        final PendingIntent notifyPendingIntent = PendingIntent.getActivity(context, 0, intent, piFlag);
        return new NotificationCompat.Builder(context, LOCATION_SERVICE_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(BaseApp.LOCATION_SERVICE_DESCRIPTION_MESSAGE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(icon)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(notifyPendingIntent)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
    }

    /**
     * Метод формирующий уведомление и отображающий его в шторке уведомлений
     *
     * @param context        контекст
     * @param message        сообщение внутри уведомления
     * @param intent         интент для вызова действия по нажатию на уведомление, может быть null
     * @param notificationId - идентификатор уведолмления
     */
    public static void showNotification(final @NonNull Context context, final @NonNull String message, final @Nullable Intent intent, final int notificationId) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle(BaseApp.NOTIFICATION).setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText(Html.fromHtml(message))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE);

        if (intent != null) {
            final int piFlag;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                piFlag = PendingIntent.FLAG_IMMUTABLE;
            } else {
                piFlag = PendingIntent.FLAG_UPDATE_CURRENT;
            }
            final PendingIntent pi = PendingIntent.getActivity(context, 0, intent, piFlag);
            builder.setContentIntent(pi);
        }
        final Notification notification = builder.build();
        notificationManager.notify(notificationId, notification);
    }
}
