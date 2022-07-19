package ru.mobnius.simple_core.utils;

import android.graphics.PointF;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

public class TouchUtil {
    /**
     * @param event обработчик прикосновения
     * @return расстояние между двумя точками прикосновения
     */
    public static float spaceCalculation(final @NonNull MotionEvent event) {
        final float x = event.getX(0) - event.getX(1);
        final float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Установка в объект {@param point} точки координаты посередине между двумя точками прикосновения к экрану
     *
     * @param point объект для хранения координат
     * @param event объект хранящий информацию о прикосновении к экрану.
     */
    public static void midPoint(final @NonNull PointF point, final @NonNull MotionEvent event) {
        final float x = event.getX(0) + event.getX(1);
        final float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
