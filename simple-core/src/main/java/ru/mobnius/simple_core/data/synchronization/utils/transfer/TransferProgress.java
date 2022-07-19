package ru.mobnius.simple_core.data.synchronization.utils.transfer;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * статус передачи данных
 */
public class TransferProgress {
    /**
     * процент выполнения
     */
    public final double percent;
    /**
     * скорость передачи данных
     */
    @NonNull
    private final TransferSpeed speed;
    /**
     * информация о переданных данных
     */
    @NonNull
    public final TransferData transferData;
    /**
     * Время оставшее до завершения
     */
    private final long time;

    private TransferProgress(final double percent,
                             final @NonNull TransferSpeed speed,
                             final @NonNull TransferData transferData,
                             final long time) {
        this.percent = percent;
        this.speed = speed;
        this.transferData = transferData;
        this.time = time;
    }

    @NonNull
    public static TransferProgress getInstance(final double percent,
                                               final @NonNull TransferSpeed speed,
                                               final @NonNull TransferData transferData,
                                               final long time) {
        return new TransferProgress(percent, speed, transferData, time);
    }

    @NonNull
    public static TransferProgress getEmptyInstance() {
        return new TransferProgress(0D,
                TransferSpeed.getEmptyInstance(),
                TransferData.getEmptyInstance(),
                System.currentTimeMillis());
    }

    @NonNull
    @Override
    public String toString() {
        if (percent >= 100) {
            return "обработка данных...";
        } else {
            final Date date = new Date(time);
            final DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            return "~" + formatter.format(date) + "(" + speed + ")";
        }
    }
}
