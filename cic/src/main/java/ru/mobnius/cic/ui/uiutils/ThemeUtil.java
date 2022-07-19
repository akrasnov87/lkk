package ru.mobnius.cic.ui.uiutils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import ru.mobnius.cic.R;
import ru.mobnius.simple_core.data.GlobalSettings;

public class ThemeUtil {

    /**
     * Смена цветовой схемы приложения для легкой идентификации стенда
     */
    public static void changeColor(final @NonNull AppCompatActivity activity) {
        switch (GlobalSettings.ENVIRONMENT){
            case GlobalSettings.ENVIRONMENT_DEV:
                activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.colorSecondaryAccent));
                activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colorSecondaryAccent));
                break;
            case GlobalSettings.ENVIRONMENT_TEST:
                activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.colorAccent));
                activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colorAccent));
                break;
            case GlobalSettings.ENVIRONMENT_DEMO:
                activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.darkGreen));
                activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.darkGreen));
                break;
            default:
                activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
                activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
                break;
        }
    }
}
