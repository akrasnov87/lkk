package ru.mobnius.simple_core.data.authorization;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.data.credentials.BasicCredentials;
import ru.mobnius.simple_core.data.credentials.BasicUser;
import ru.mobnius.simple_core.preferences.GeneralPreferences;
import ru.mobnius.simple_core.utils.AuthUtil;
import ru.mobnius.simple_core.utils.ClaimsUtil;
import ru.mobnius.simple_core.utils.LongUtil;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Авторизация
 */
public class Authorization {

    @Nullable
    private static Authorization authorization;
    private static final String AGENT_CLAIM = "user";

    @Nullable
    public BasicUser user;
    private int authStatus = BaseApp.RESPONSE_FAIL;

    private Authorization() {
        user = new BasicUser(new BasicCredentials(BaseApp.ERROR_ENG, BaseApp.ERROR_ENG), LongUtil.MINUS, StringUtil.EMPTY);
    }

    /**
     * созданеие экземпляра класса
     *
     * @return Объект для реализации авторизации пользователя
     */
    public static Authorization createInstance() {
        if (authorization != null) {
            return authorization;
        } else {
            return authorization = new Authorization();
        }
    }

    public static boolean createDefaultInstance(final @NonNull Context context, final @NonNull AuthorizationMeta authorizationMeta) {
        authorization = new Authorization();
        final BasicCredentials credentials = BasicCredentials.decode(authorizationMeta.getToken());
        if (credentials == null) {
            return false;
        }
        final BasicUser basicUser = new BasicUser(credentials, authorizationMeta.getUserId(), authorizationMeta.getClaims());
        return authorization.setUser(context, basicUser);
    }

    public static boolean createPinOrTouchInstance(Context context) {
        authorization = new Authorization();
        final String login = AuthUtil.getSingleUserLogin(context);
        if (StringUtil.isEmpty(login)) {
            return false;
        }
        final BasicUser basicUser = AuthUtil.readUser(context, login);
        if (basicUser == null) {
            return false;
        }
        return authorization.setUser(context, basicUser);
    }

    @Nullable
    public static Authorization getInstance() {
        return authorization;
    }

    /**
     * Авторизован пользователь или нет
     *
     * @return true - пользователь авторизован
     */
    public boolean isAuthorized() {
        return authStatus == BaseApp.RESPONSE_OK;
    }

    /**
     * Обновление пользователя
     *
     * @return true если сохранение пользовеателя прошло успешно
     */
    public boolean setUser(final @NonNull Context context, final @NonNull BasicUser basicUser) {
        user = basicUser;
        final boolean saveUserSuccess = AuthUtil.saveUser(context, basicUser);
        if (saveUserSuccess) {
            authStatus = BaseApp.RESPONSE_OK;
            if (GeneralPreferences.getInstance() != null) {
                GeneralPreferences.getInstance().setIsSingleUser(AuthUtil.isSingleUser(context));
            }
        }
        return saveUserSuccess;
    }

    /**
     * Является инспектором
     *
     * @return true - авторизованный пользователь является инспектором
     */
    public boolean isAgent() {
        if (user != null) {
            final ClaimsUtil util = new ClaimsUtil(user.claims);
            return util.isExists(AGENT_CLAIM);
        }

        return false;
    }


    /**
     * Сброс авторизации
     */
    public void reset() {
        user = null;
        authStatus = BaseApp.RESPONSE_FAIL;
    }

    @VisibleForTesting
    public static final long TEST_ID = Long.MAX_VALUE;

    @VisibleForTesting
    public static void createTest() {
        authorization = new Authorization();
        final BasicCredentials basicCredentials = new BasicCredentials("iphone", "" + "1234");
        authorization.user = new BasicUser(basicCredentials, 69L, AGENT_CLAIM);
        authorization.authStatus = BaseApp.RESPONSE_OK;

    }

}
