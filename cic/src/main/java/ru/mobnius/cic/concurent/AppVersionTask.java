package ru.mobnius.cic.concurent;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Callable;

import ru.mobnius.cic.ui.model.concurent.AppVersionResult;
import ru.mobnius.simple_core.data.GlobalSettings;
import ru.mobnius.simple_core.data.Meta;
import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Класс для получения актуальной на данный момент выерсии с сервера
 * в дополнительном потоке
 */
public class AppVersionTask implements Callable<AppVersionResult> {
    @NonNull
    public final String versionName;
    @NonNull
    private final static String VERSION_JSON_KEY = "version";
    @NonNull
    private final static String UPLOAD_VERSION = "/upload/version/";

    public AppVersionTask(final @NonNull String versionName) {
        this.versionName = versionName;
    }

    @Override
    public AppVersionResult call() throws Exception {
        final URL url = new URL(GlobalSettings.getConnectUrl() + UPLOAD_VERSION + versionName);
        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {

            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(BaseApp.CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(BaseApp.CONNECTION_TIMEOUT);
            if (urlConnection.getResponseCode() != Meta.OK) {
                return new AppVersionResult(StringUtil.EMPTY, true);
            }
            final InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            final Scanner s = new Scanner(in).useDelimiter("\\A");
            final String result = s.hasNext() ? s.next() : "";

            final JSONObject object = new JSONObject(result);
            final String version = object.getString(VERSION_JSON_KEY);
            return new AppVersionResult(version, false);
        } catch (Exception e) {
            e.printStackTrace();
            return new AppVersionResult(StringUtil.EMPTY, true);
        } finally {
            urlConnection.disconnect();
        }
    }
}
