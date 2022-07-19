package ru.mobnius.simple_core.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKey;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ru.mobnius.simple_core.data.GlobalSettings;
import ru.mobnius.simple_core.data.Meta;
import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.data.authorization.Authorization;
import ru.mobnius.simple_core.data.authorization.AuthorizationMeta;
import ru.mobnius.simple_core.data.credentials.BasicCredentials;
import ru.mobnius.simple_core.data.credentials.BasicUser;

public class AuthUtil {

    private final static String USERS_ID_JSON_KEY = "id";
    private final static String USER_JSON_KEY = "user";
    private final static String CLAIMS_JSON_KEY = "claims";
    private final static String TIME_JSON_KEY = "time";
    private final static String TOKEN_JSON_KEY = "token";
    private final static String META_JSON_KEY = "meta";
    private final static String MSG_JSON_KEY = "msg";
    private final static String SUCCESS_JSON_KEY = "success";
    private final static String RESULT_JSON_KEY = "result";
    private final static String RECORDS_JSON_KEY = "records";

    private final static String USER_NAME_URL_PARAM = "UserName=";
    private final static String AMPERSANT = "&";
    private final static String PASSWORD_URL_PARAM = "Password=";
    private final static String VERSION_URL_PARAM = "Version=";
    private final static String AUTH_URL_ADDRESS_END = "/auth";
    private final static String C_EMAIL_URL_PARAM = "c_email=";
    private final static String RESTORE_AUTH_URL_ADDRESS_END = "/user/password-instruction-reset";

    private final static String POST = "POST";
    private final static String CONTENT_TYPE_KEY = "Content-Type";
    private final static String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";
    private final static String CONTENT_LENGTH_KEY = "Content-Length";

    private final static String USERS_DIRECTORY = "USERS_DIRECTORY";

    /**
     * Метод проверяющий является ли зарегестрированный
     * пользователь единственным пользователем приложения
     *
     * @return true если файл с информацией о пользователе существует и он единственный
     */
    public static boolean isSingleUser(final @NonNull Context context) {
        try {
            final File dir = context.getFilesDir();
            if (dir == null || !dir.exists()) {
                FileUtils.forceMkdir(dir);
                return false;
            }

            final File subdir = new File(dir, USERS_DIRECTORY);
            if (!subdir.exists()) {
                FileUtils.forceMkdir(subdir);
                return false;
            }

            File[] list = subdir.listFiles();
            if (list == null) {
                return false;
            }
            return list.length == 1;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Сохраняем или обновляем информацию о пользователе в файл название которого
     * соответствует логину пользователя. Если такой файл уже существует то он
     * удаляется и записывается заново.
     *
     * @return успех операции
     */
    public static boolean saveUser(final @NonNull Context context, final @NonNull BasicUser basicUser) {
        if (basicUser.getUserId() == LongUtil.MINUS) {
            return false;
        }
        try {
            final MasterKey mainKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            final File dir = context.getFilesDir();
            FileUtils.forceMkdir(dir);
            final File subdir = new File(dir, USERS_DIRECTORY);
            FileUtils.forceMkdir(subdir);
            final File file = new File(subdir, basicUser.getCredentials().login);
            if (file.exists()) {
                FileUtils.deleteQuietly(file);
            }
            final EncryptedFile encryptedFile = new EncryptedFile.Builder(
                    context,
                    new File(subdir, basicUser.getCredentials().login),
                    mainKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();
            final JSONObject json = new JSONObject();
            json.put(USERS_ID_JSON_KEY, basicUser.getUserId());
            json.put(CLAIMS_JSON_KEY, basicUser.claims);
            json.put(TIME_JSON_KEY, System.currentTimeMillis());
            json.put(TOKEN_JSON_KEY, basicUser.getCredentials().getToken());
            final byte[] fileContent = json.toString()
                    .getBytes(StandardCharsets.UTF_8);
            final OutputStream outputStream = encryptedFile.openFileOutput();
            outputStream.write(fileContent);
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (GeneralSecurityException | JSONException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Пытаемся прочитать ранее сохранненую информацию о пользователе
     * в файле имя которго соответствует {@param login}
     *
     * @return в случае успешного чтения файла объект {@link BasicUser},
     * в противном случае null
     */
    @Nullable
    public static BasicUser readUser(final @NonNull Context context, final @NonNull String login) {
        try {
            final MasterKey mainKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            final File dir = context.getFilesDir();
            final File subdir = new File(dir, USERS_DIRECTORY);
            final File userFile = new File(subdir, login);
            if (!userFile.exists()) {
                return null;
            }
            final EncryptedFile encryptedFile = new EncryptedFile.Builder(
                    context,
                    userFile,
                    mainKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();

            final InputStream inputStream = encryptedFile.openFileInput();
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int nextByte = inputStream.read();
            while (nextByte != -1) {
                byteArrayOutputStream.write(nextByte);
                nextByte = inputStream.read();
            }

            final byte[] plaintext = byteArrayOutputStream.toByteArray();
            final String user = new String(plaintext, StandardCharsets.UTF_8);
            final JSONObject jsonObject = new JSONObject(user);
            final BasicCredentials basicCredentials = BasicCredentials.decode(jsonObject.getString(TOKEN_JSON_KEY));
            if (basicCredentials == null) {
                return null;
            }
            final long userId = jsonObject.getLong(USERS_ID_JSON_KEY);
            final String claims = jsonObject.getString(CLAIMS_JSON_KEY);
            return new BasicUser(basicCredentials, userId, claims);
        } catch (JSONException | GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Получение логина пользователя.
     *
     * @return в случае если файл существует и он единственный
     * возвращает логин пользователя, в противном случае пустую строку
     */
    @NonNull
    public static String getSingleUserLogin(final @NonNull Context context) {
        try {
            final File dir = context.getFilesDir();
            if (dir == null || !dir.exists()) {
                FileUtils.forceMkdir(dir);
                return StringUtil.EMPTY;
            }

            final File subdir = new File(dir, USERS_DIRECTORY);
            if (!subdir.exists()) {
                FileUtils.forceMkdir(subdir);
                return StringUtil.EMPTY;
            }

            final File[] users = subdir.listFiles();
            if (users == null || users.length != 1) {
                return StringUtil.EMPTY;
            }
            return users[0].getName();
        } catch (IOException e) {
            e.printStackTrace();
            return StringUtil.EMPTY;
        }
    }


    /**
     * Получение логинов пользователей.
     *
     * @return в случае если файлы существуют
     * возвращает список логинов пользователей, в противном случае пустой список
     */
    @NonNull
    public static List<String> getAllUsersLogin(final @NonNull Context context) {
        final List<String> logins = new ArrayList<>();
        try {
            final File dir = context.getFilesDir();
            if (dir == null || !dir.exists()) {
                FileUtils.forceMkdir(dir);
                return logins;
            }

            final File subdir = new File(dir, USERS_DIRECTORY);
            if (!subdir.exists()) {
                FileUtils.forceMkdir(subdir);
                return logins;
            }

            final File[] users = subdir.listFiles();
            if (users == null) {
                return logins;
            }
            for (final File user : users) {
                if (StringUtil.isNotEmpty(user.getName())) {
                    logins.add(user.getName());
                }
            }
            return logins;
        } catch (IOException e) {
            e.printStackTrace();
            return logins;
        }
    }

    /**
     * Получение объекта пользователя.
     *
     * @return в случае если файл существует и он единственный
     * возвращает {@link BasicUser}, в противном случае null
     */
    @Nullable
    public static BasicUser getSingleUser(final @NonNull Context context) {
        final String singleUserLogin = getSingleUserLogin(context);
        if (StringUtil.isNotEmpty(singleUserLogin)) {
            return readUser(context, singleUserLogin);
        }
        return null;
    }

    /**
     * Получение списка объектов пользователей.
     *
     * @return в случае если файлы существуют возвращает
     * список {@link BasicUser}, в противном случае пустой список
     */
    @NonNull
    public static List<BasicUser> getAllUsers(final @NonNull Context context) {
        final List<BasicUser> users = new ArrayList<>();
        final List<String> logins = getAllUsersLogin(context);
        for (final String login : logins) {
            final BasicUser user = readUser(context, login);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    /**
     * Создание запроса на сервер для выполнения авторизации
     *
     * @param versionName номер версии
     * @param login       логин
     * @param password    пароль
     * @return мета информация в результате запроса
     */
    @NonNull
    public static AuthorizationMeta requestAuth(final @NonNull String versionName, final @NonNull String login, final @NonNull String password) {
        HttpURLConnection urlConnection = null;
        Scanner scanner = null;
        final String urlParams = USER_NAME_URL_PARAM + encodeValue(login)
                + AMPERSANT + PASSWORD_URL_PARAM + encodeValue(password) + AMPERSANT + VERSION_URL_PARAM + versionName;
        final byte[] postData = urlParams.getBytes(StandardCharsets.UTF_8);
        final int postDataLength = postData.length;
        try {
            final URL url = new URL(GlobalSettings.getConnectUrl() + AUTH_URL_ADDRESS_END);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(BaseApp.CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(BaseApp.CONNECTION_TIMEOUT);
            urlConnection.setRequestMethod(POST);
            urlConnection.setRequestProperty(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
            urlConnection.setRequestProperty(CONTENT_LENGTH_KEY, String.valueOf(postDataLength));
            urlConnection.setDoOutput(true);
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setUseCaches(false);
            urlConnection.getOutputStream().write(postData);
            final InputStream stream;
            if (urlConnection.getResponseCode() == Meta.OK) {
                stream = urlConnection.getInputStream();
            } else {
                stream = urlConnection.getErrorStream();
            }
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(stream);
            scanner = new Scanner(bufferedInputStream).useDelimiter("\\A");
            final String responseText = scanner.hasNext() ? scanner.next() : StringUtil.EMPTY;
            try {
                return convertAuthResponseToMeta(responseText);
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
                return new AuthorizationMeta(Meta.ERROR_SERVER, BaseApp.AUTH_RESPONSE_TRANSFORMATION_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new AuthorizationMeta(Meta.ERROR_SERVER, BaseApp.ERROR_CREATING_AUTH_REQUEST);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    /**
     * Создание запроса на сервер для восстановления логина и пароля
     *
     * @param eMailAddress электронная почта для получения информации для восстановления
     * @return мета информация в результате запроса
     */
    @NonNull
    public static Meta restoreAuthInfo(final @NonNull String eMailAddress) {
        HttpURLConnection urlConnection = null;
        Scanner scanner = null;
        try {
            final String urlParams = C_EMAIL_URL_PARAM + encodeValue(eMailAddress);
            final byte[] postData = urlParams.getBytes(StandardCharsets.UTF_8);
            final int postDataLength = postData.length;

            final URL url = new URL(GlobalSettings.getConnectUrl() + RESTORE_AUTH_URL_ADDRESS_END);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(BaseApp.CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(BaseApp.CONNECTION_TIMEOUT);
            urlConnection.setRequestMethod(POST);
            urlConnection.setRequestProperty(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
            urlConnection.setRequestProperty(CONTENT_LENGTH_KEY, String.valueOf(postDataLength));
            urlConnection.setDoOutput(true);
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setUseCaches(false);
            urlConnection.getOutputStream().write(postData);
            final InputStream stream;
            if (urlConnection.getResponseCode() == Meta.OK) {
                stream = urlConnection.getInputStream();
            } else {
                stream = urlConnection.getErrorStream();
            }
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(stream);
            scanner = new Scanner(bufferedInputStream).useDelimiter("\\A");
            final String responseText = scanner.hasNext() ? scanner.next() : StringUtil.EMPTY;
            try {
                return convertRestoreResponseToMeta(responseText);
            } catch (JSONException jsonException) {
                return new Meta(Meta.ERROR_SERVER, BaseApp.SERVER_JSON_PARSE_ERROR);
            }
        } catch (IOException e) {
            return new Meta(Meta.ERROR_SERVER, BaseApp.ERROR_CREATING_RESTORE_REQUEST);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

        }
    }

    /**
     * Преобразование статуса ответа об авторизации в мета-информацию
     *
     * @param response ответ от сервера в формате JSON
     * @return мета информация
     */
    @NonNull
    private static AuthorizationMeta convertAuthResponseToMeta(final @NonNull String response) throws JSONException {
        final JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has(META_JSON_KEY)) {
            final JSONObject meta = jsonObject.getJSONObject(META_JSON_KEY);
            if (meta.has(SUCCESS_JSON_KEY)) {
                final boolean success = meta.getBoolean(SUCCESS_JSON_KEY);
                if (!success) {
                    String message = BaseApp.SERVER_JSON_PARSE_ERROR;
                    if (meta.has(MSG_JSON_KEY)) {
                        message = meta.getString(MSG_JSON_KEY);
                    }
                    return new AuthorizationMeta(Meta.NOT_AUTHORIZATION, message, null, null, null);
                }
            }
        } else {
            if (jsonObject.has(USER_JSON_KEY) && jsonObject.has(TOKEN_JSON_KEY)) {
                final JSONObject user = jsonObject.getJSONObject(USER_JSON_KEY);
                final String token = jsonObject.getString(TOKEN_JSON_KEY);
                if (user.has(USERS_ID_JSON_KEY) && user.has(CLAIMS_JSON_KEY)) {
                    return new AuthorizationMeta(Meta.OK, BaseApp.USER_AUTHORIZED, token, user.getString(CLAIMS_JSON_KEY), user.getLong(USERS_ID_JSON_KEY));
                }
            }
        }
        return new AuthorizationMeta(Meta.ERROR_SERVER, BaseApp.AUTH_RESULT_FORMAT_NOT_JSON, null, null, null);
    }

    /**
     * Преобразование статуса ответа о восстановлении
     * логина и пароля в мета-информацию
     *
     * @param response ответ от сервера в формате JSON
     * @return мета информация
     */
    @NonNull
    private static Meta convertRestoreResponseToMeta(final @NonNull String response) throws JSONException {
        final JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has(META_JSON_KEY)) {
            final JSONObject meta = jsonObject.getJSONObject(META_JSON_KEY);
            if (meta.has(SUCCESS_JSON_KEY)) {
                final boolean success = meta.getBoolean(SUCCESS_JSON_KEY);
                if (success) {
                    return new Meta(Meta.OK, BaseApp.AUTH_RESTORE_SUCCESS);
                } else {
                    if (jsonObject.has(RESULT_JSON_KEY)) {
                        final JSONObject result = jsonObject.getJSONObject(RESULT_JSON_KEY);
                        if (result.has(RECORDS_JSON_KEY)) {
                            final String record = result.getString(RECORDS_JSON_KEY);
                            return new Meta(Meta.ERROR_SERVER, record);
                        }
                    }
                }
            }
        }
        return new Meta(Meta.ERROR_SERVER, BaseApp.SERVER_JSON_PARSE_ERROR);
    }

    /**
     * Метод для кодирование url в формат для html
     *
     * @param value строка url запроса
     * @return кодированная строка
     */
    @NonNull
    private static String encodeValue(final @NonNull String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException | IllegalCharsetNameException e) {
            return value;
        }
    }

    /**
     * Очистка кэшированных данных
     *
     * @param all удалить все файлы безопасности или нет
     */
    public static void clear(final @NonNull Context context, final boolean all) {
        final File dir = context.getFilesDir();
        if (dir == null || !dir.exists()) {
            return;
        }
        final File subdir = new File(dir, USERS_DIRECTORY);
        if (!subdir.exists()) {
            return;
        }
        if (all) {
            FileUtil.deleteQuietly(subdir);
            return;
        }
        if (Authorization.getInstance() == null || Authorization.getInstance().user == null) {
            return;
        }
        final String login = Authorization.getInstance().user.getCredentials().login;
        if (StringUtil.isNotEmpty(login)) {
            final File file = new File(subdir, login);
            if (file.exists()) {
                FileUtil.deleteQuietly(file);
            }
        }
    }
}

