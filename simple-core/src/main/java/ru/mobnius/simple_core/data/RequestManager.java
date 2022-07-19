package ru.mobnius.simple_core.data;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.greendao.annotation.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.data.rpc.RPCItem;
import ru.mobnius.simple_core.data.rpc.RPCResult;
import ru.mobnius.simple_core.data.rpc.ServerTimeRpcResult;
import ru.mobnius.simple_core.data.rpc.SingleItemQuery;
import ru.mobnius.simple_core.utils.StringUtil;

public class RequestManager {
    /**
     * заголовок для авторизации по умолчанию
     */
    public final static String AUTHORIZATION_HEADER = "rpc-authorization";

    private final static String POST = "POST";
    private final static String CONTENT_TYPE_KEY = "Content-Type";
    private final static String CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";
    private final static String REQUEST_PROPERTY_ACCEPT_KEY = "Accept";
    private final static String REQUEST_PROPERTY_ACCEPT_VALUE = "application/json";
    private final static String CONTENT_LENGTH_KEY = "Content-Length";
    private final static String REQUEST_SERVER_TIME_ACTION = "shell";
    private final static String REQUEST_SERVER_TIME_METHOD = "servertime";
    private final static String OPEN_SQUARE_BRACKETS = "[";
    private final static String URL_RPC = "/rpc";
    private final static String STRING_START_DELIMITTER = "\\A";


    /**
     * Выполнение RPC запроса
     *
     * @param baseUrl  настройки соединения
     * @param token    токен-авторизация
     * @param postData входные данные
     * @return возвращается строка если возникла ошибка, либо объект RPCResult[]
     * @throws IOException общая ошибка
     */
    @Nullable
    public static RPCResult[] rpc(final @NonNull String baseUrl,
                                  final @NonNull String token,
                                  final @NonNull byte[] postData) throws IOException {
        final URL url = new URL(baseUrl + URL_RPC);
        RPCResult[] rpcResults = null;
        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setRequestMethod(POST);
            urlConnection.setRequestProperty(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
            urlConnection.setRequestProperty(REQUEST_PROPERTY_ACCEPT_KEY, REQUEST_PROPERTY_ACCEPT_VALUE);
            urlConnection.setRequestProperty(AUTHORIZATION_HEADER, token);
            urlConnection.setRequestProperty(CONTENT_LENGTH_KEY, String.valueOf(postData.length));
            urlConnection.setDoOutput(true);
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(BaseApp.CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(BaseApp.CONNECTION_TIMEOUT);

            urlConnection.getOutputStream().write(postData);

            final InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            final Scanner s = new Scanner(in).useDelimiter("\\A");
            final String serverResult = s.hasNext() ? s.next() : StringUtil.EMPTY;

            try {
                rpcResults = RPCResult.createInstanceByGson(serverResult);
            } catch (Exception formatExc) {
                formatExc.printStackTrace();
            }
        } catch (Exception innerErr) {
            innerErr.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return rpcResults;
    }

    /**
     * Выполнение RPC запроса
     *
     * @param token токен-авторизация
     * @return возвращается строка если возникла ошибка, либо объект RPCResult[]
     * @throws IOException общая ошибка
     */
    @NonNull
    public static RPCResult[] getUserInfo(final @NonNull String token) throws IOException {
        HttpURLConnection urlConnection = null;
        Scanner scanner = null;
        InputStream inputStream = null;
        try {
            final URL url = new URL(GlobalSettings.getConnectUrl() + "/user/profile?" + AUTHORIZATION_HEADER + "=" + token);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setConnectTimeout(BaseApp.CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(BaseApp.CONNECTION_TIMEOUT);

            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            scanner = new Scanner(inputStream).useDelimiter(STRING_START_DELIMITTER);
            String serverResult = scanner.hasNext() ? scanner.next() : StringUtil.EMPTY;
            serverResult = serverResult.replaceAll(StringUtil.SPACE, StringUtil.EMPTY);

            final RPCResult[] result;
            if (serverResult.indexOf(OPEN_SQUARE_BRACKETS) == 0) {
                result = new Gson().fromJson(serverResult, RPCResult[].class);
            } else {
                result = new RPCResult[1];
                result[0] = new Gson().fromJson(serverResult, RPCResult.class);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return new RPCResult[0];
    }

    /**
     * RPC запрос
     *
     * @param baseUrl настройки соединения
     * @param token   токен-авторизация
     * @param action  сущность
     * @param method  метод
     * @param data    данные
     * @return возвращается строка если возникла ошибка, либо объект RPCResult[]
     */
    @Nullable
    public static RPCResult[] rpc(final @NonNull String baseUrl,
                                  final @NonNull String token,
                                  final @NonNull String action,
                                  final @NonNull String method,
                                  final @NonNull SingleItemQuery data) throws IOException {
        return rpc(baseUrl, token, action, method, (Object) data);
    }

    /**
     * RPC запрос
     *
     * @param baseUrl настройки соединения
     * @param token   токен-авторизация
     * @param action  сущность
     * @param method  метод
     * @param data    данные
     * @return возвращается строка если возникла ошибка, либо объект RPCResult[]
     */
    @Nullable
    public static RPCResult[] rpc(final @NonNull String baseUrl,
                                  final @NonNull String token,
                                  final @NonNull String action,
                                  final @NonNull String method,
                                  final @NonNull Object data) throws IOException {
        final Object[] arr = new Object[1];
        arr[0] = data;
        final RPCItem item = new RPCItem(action, method, arr);
        final String urlParams = item.toJsonString();
        final byte[] postData = urlParams.getBytes(StandardCharsets.UTF_8);

        return rpc(baseUrl, token, postData);
    }

    @NonNull
    public static ServerTimeRpcResult[] rpcServerTime(final @NonNull String token, final @NonNull Object data) {
        final Object[] arr = new Object[1];
        arr[0] = data;
        final RPCItem item = new RPCItem(REQUEST_SERVER_TIME_ACTION, REQUEST_SERVER_TIME_METHOD, arr);
        final String urlParams = item.toJsonString();
        final byte[] postData = urlParams.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection urlConnection = null;
        Scanner scanner = null;
        InputStream inputStream = null;
        try {
            final URL url = new URL(GlobalSettings.getConnectUrl() + URL_RPC);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod(POST);
            urlConnection.setRequestProperty(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
            urlConnection.setRequestProperty(REQUEST_PROPERTY_ACCEPT_KEY, REQUEST_PROPERTY_ACCEPT_VALUE);
            urlConnection.setRequestProperty(AUTHORIZATION_HEADER, token);
            urlConnection.setRequestProperty(CONTENT_LENGTH_KEY, String.valueOf(postData.length));
            urlConnection.setDoOutput(true);
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(BaseApp.CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(BaseApp.CONNECTION_TIMEOUT);
            urlConnection.getOutputStream().write(postData);

            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            scanner = new Scanner(inputStream).useDelimiter(STRING_START_DELIMITTER);
            String serverResult = scanner.hasNext() ? scanner.next() : StringUtil.EMPTY;
            serverResult = serverResult.replaceAll(StringUtil.SPACE, StringUtil.EMPTY);

            final ServerTimeRpcResult[] result;
            if (serverResult.indexOf(OPEN_SQUARE_BRACKETS) == 0) {
                result = new Gson().fromJson(serverResult, ServerTimeRpcResult[].class);
            } else {
                result = new ServerTimeRpcResult[1];
                result[0] = new Gson().fromJson(serverResult, ServerTimeRpcResult.class);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return new ServerTimeRpcResult[0];
    }
}
