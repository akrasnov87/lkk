package ru.mobnius.simple_core.utils;

import android.location.Location;

import java.util.Date;
import java.util.Locale;

public class LocationUtil {
    /**
     * Создание объекта Location
     * @param n_longitude долгота
     * @param n_latitude широта
     * @return объект Location
     */
    public static Location getLocation(double n_longitude, double n_latitude) {
        Location location = new Location("CODE");
        location.setLatitude(n_latitude);
        location.setLongitude(n_longitude);
        location.setTime(new Date().getTime());
        return location;
    }

    public static String toString(Location location, int decimals) {
        return String.format(new Locale("ru", "RU"), "%." + decimals + "f ; %." + decimals + "f", location.getLatitude(), location.getLongitude());
    }

    public static String toString(Location location) {
        return toString(location, 4);
    }

}