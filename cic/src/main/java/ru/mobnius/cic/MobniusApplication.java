package ru.mobnius.cic;

import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.data.manager.ManualSynchronization;
import ru.mobnius.cic.data.storage.AuditUtil;
import ru.mobnius.cic.data.storage.DbOpenHelper;
import ru.mobnius.cic.data.storage.models.ClientErrors;
import ru.mobnius.cic.data.storage.models.DaoMaster;
import ru.mobnius.cic.data.storage.models.DaoSession;
import ru.mobnius.cic.di.AppComponent;
import ru.mobnius.cic.di.DaggerAppComponent;
import ru.mobnius.cic.ui.uiutils.NotificationUtil;
import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.data.GlobalSettings;
import ru.mobnius.simple_core.data.Version;
import ru.mobnius.simple_core.data.authorization.Authorization;
import ru.mobnius.simple_core.data.configuration.PreferencesManager;
import ru.mobnius.simple_core.data.credentials.BasicUser;
import ru.mobnius.simple_core.data.synchronization.utils.transfer.Transfer;
import ru.mobnius.simple_core.preferences.GeneralPreferences;
import ru.mobnius.simple_core.utils.AuthUtil;
import ru.mobnius.simple_core.utils.DateUtil;
import ru.mobnius.simple_core.utils.LongUtil;
import ru.mobnius.simple_core.utils.StringUtil;
import ru.mobnius.simple_core.utils.VersionUtil;

/**
 * Основной класс приложения
 */
public class MobniusApplication extends BaseApp {

    private static AppComponent appComponent;
    /**
     * Строковые константы
     */
    public static String STORAGE = "Хранилище";
    public static String LOCATION = "Местополжение";
    public static String CONTACTS = "Контакты";
    public static String CAMERA = "Камера";
    public static String RECORD_AUDIO = "Запись аудио";
    public static String BACKGROUND_LOCATION = "Местоположение в фоновом режиме";
    public static String AUTH_ERROR = "Ошибка авторизации";
    public static String AUTH_SUCCESS = "Успешная авторизация";
    public static String SERVER_RESPONSE_ERROR = "Ошибка на сервере";
    public static String WRONG_TIMEZONE = "Часовой пояс устройства отличается от Московского времени. Необходимо изменить в настройках устройства.";
    public static String WRONG_DEVICE_TIME = "Время устройства отличается от серверного времени. Необходимо изменить в настройках устройства.";
    public static String USER_ENG = "user";
    public static String APP_NAME = "Mobnius Electric";
    public static String METER_READINGS_ACT_NAME = "Акт снятия контрольных показаний";
    public static String POINTS = "Точки маршрутов";
    public static String ROUTES = "Маршруты";
    public static String SUBSCR_NAME = "ФИО/Наименование";
    public static String ADDRESS = "Адрес";
    public static String DEVICE_NUMBER = "Номер ПУ";
    public static String DEVICE_TYPE_NAME = "Тип ПУ";
    public static String ACCOUNT_NUMBER = "Номер лицевого счета";
    public static String ROUTE_NAME = "Наименование маршрута";
    public static String NOTICE = "Примечание";
    public static String START_DATE = "Дата начала";
    public static String END_DATE = "Дата окончания";
    public static String CAN_NOT_EDIT_SYNCED_RESULT = "Нельзя редактировать синхронизированные результат";
    public static String MUST_MAKE_PHOTO_OF = "Необходимо сделать следующие фото: ";
    public static String METERS_PHOTO = " - показание\n";
    public static String SEALS_PHOTO = " - пломба\n";
    public static String DEVICE_PHOTO = " - прибор учета\n";
    public static String ALL_NECESSARY_PHOTOS_ARE_MADE = "Все необходимые фото сделаны";
    public static String RESULT_STATUS = "Статус: ";
    public static String RESULT_CAUSE = " , Причина: ";
    public static String DOWNLOADING_DICTIONARY_DATA = "Идет загрузка справочников";
    public static String DOWNLOADING_GENERAL_DATA = "Идет загрузка общих данных";
    public static String ESTABLISHING_CONNECTION = "Устанавливаем соединение...";
    public static String CONNECTION_ESTABLISHED = "Соединение успешно установлено";
    public static String CONNECTION_ERROR = "Ошибка соединения \n";
    public static String CONNECTION_LOST = "Соедниение потеряно";
    public static String TID_IS_EMPTY = "Идентификатор трансфера пустой";
    public static String CURRENT_LOCATION = "Текущее местоположение";
    public static String LOCATION_SET_MANUALLY = "Местоположение установлено вручную";
    public static String DICTIONARY_SENT_SIZE = "Справочники, отправлено: ";
    public static String GENERAL_SENT_SIZE = "Общие, отправлено: ";
    public static String FILES_SENT_SIZE = "Файлы, отправлено: ";
    public static String DICTIONARY_RECEIVED_SIZE = "Справочники, получено: ";
    public static String GENERAL_RECEIVED_SIZE = "Общие, получено: ";
    public static String FILES_RECEIVED_SIZE = "Файлы, получено: ";

    public MobniusApplication() {

        GlobalSettings.ENVIRONMENT = GlobalSettings.ENVIRONMENT_DEV;
        GlobalSettings.BASE_URL = "https://lkk-sk.it-serv.ru";
        Transfer.INTERVAL = 1000;
        NotificationUtil.LOCATION_SERVICE_NOTIFICATION_CHANNEL_ID = "Mobnius_Electric_Channel";
        Version.BIRTH_DAY = new GregorianCalendar(2022, 1, 21).getTime();

        //TODO: пока бесполезный код, но можно доработать чтобы у пользователя была возможность выбора url
        //см. закоментированный код в SettingsFragment, там всё ок, нужно только доработать сброс авторизации
        GlobalSettings.availableUrls.add("https://lkk-sk.it-serv.ru");
        GlobalSettings.availableUrls.add("http://lkk-sk.eivk.mrsk-sk.ru");
        GlobalSettings.availableUrls.add("http://10.10.6.100:5000");
        GlobalSettings.availableUrls.add("https://lkk-sk-public.eivk.mrsk-sk.ru");

        GlobalSettings.enviroments.add(GlobalSettings.ENVIRONMENT_RELEASE);
        GlobalSettings.enviroments.add(GlobalSettings.ENVIRONMENT_DEV);
        GlobalSettings.enviroments.add(GlobalSettings.ENVIRONMENT_DEMO);
        GlobalSettings.enviroments.add(GlobalSettings.ENVIRONMENT_TEST);
        for (int i = 0; i < GlobalSettings.enviroments.size(); i++) {
            final Map<String, Object> map = new HashMap<>();
            map.put(Names.ID, i);
            map.put(Names.NAME, GlobalSettings.enviroments.get(i));
            GlobalSettings.enviromentMap.add(map);
        }
    }

    private void initConstants() {
        STORAGE = getString(R.string.storage);
        LOCATION = getString(R.string.location);
        CONTACTS = getString(R.string.contacts);
        CAMERA = getString(R.string.camera);
        RECORD_AUDIO = getString(R.string.record_audio);
        BACKGROUND_LOCATION = getString(R.string.background_location);
        AUTH_ERROR = getString(R.string.auth_error);
        AUTH_SUCCESS = getString(R.string.auth_success);
        SERVER_RESPONSE_ERROR = getString(R.string.server_response_error);
        WRONG_TIMEZONE = getString(R.string.wrong_timezone);
        WRONG_DEVICE_TIME = getString(R.string.wrong_device_time);
        USER_ENG = getString(R.string.user_eng);
        APP_NAME = getString(R.string.app_name);
        METER_READINGS_ACT_NAME = getString(R.string.meter_readings_act_name);
        POINTS = getString(R.string.points);
        ROUTES = getString(R.string.routes);
        SUBSCR_NAME = getString(R.string.subscr_name);
        ACCOUNT_NUMBER = getString(R.string.account_number);
        ADDRESS = getString(R.string.address);
        DEVICE_NUMBER = getString(R.string.device_number);
        DEVICE_TYPE_NAME = getString(R.string.device_type);
        ROUTE_NAME = getString(R.string.route_name);
        NOTICE = getString(R.string.notice);
        START_DATE = getString(R.string.start_date);
        END_DATE = getString(R.string.end_date);
        RESULT_STATUS = getString(R.string.result_status_for_history);
        CAN_NOT_EDIT_SYNCED_RESULT = getString(R.string.can_not_edit_synced_data);
        MUST_MAKE_PHOTO_OF = getString(R.string.must_make_next_photos);
        METERS_PHOTO = getString(R.string.readings_photo_name);
        SEALS_PHOTO = getString(R.string.seal_photo_name);
        DEVICE_PHOTO = getString(R.string.device_photo_name);
        ALL_NECESSARY_PHOTOS_ARE_MADE = getString(R.string.all_necessary_photos_are_made);
        RESULT_CAUSE = getString(R.string.result_cause_for_history);
        DOWNLOADING_DICTIONARY_DATA = getString(R.string.downloading_dictionary);
        DOWNLOADING_GENERAL_DATA = getString(R.string.downloading_general_data);
        ESTABLISHING_CONNECTION = getString(R.string.establishing_connection);
        CONNECTION_ESTABLISHED = getString(R.string.connection_established);
        CONNECTION_ERROR = getString(R.string.connection_error);
        CONNECTION_LOST = getString(R.string.connection_lost);
        TID_IS_EMPTY = getString(R.string.tid_is_empty);
        CURRENT_LOCATION = getString(R.string.current_location);
        LOCATION_SET_MANUALLY = getString(R.string.location_set_manually);
        DICTIONARY_SENT_SIZE = getString(R.string.dictionary_sent_size);
        GENERAL_SENT_SIZE = getString(R.string.general_info_sent_size);
        FILES_SENT_SIZE = getString(R.string.files_sent_size);
        DICTIONARY_RECEIVED_SIZE = getString(R.string.dictionary_received_size);
        GENERAL_RECEIVED_SIZE = getString(R.string.general_info_received_size);
        FILES_RECEIVED_SIZE = getString(R.string.files_received_size);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.create();
        //TODO: этот метод можно использовать если крашлитикс перестанет работать
        //Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);
        if (GeneralPreferences.getInstance() != null) {
            //TODO: тут код который связан с возможностью выбора пользователем url
            /*
            final String serverUrl = GeneralPreferences.getInstance().getServerUrl();
            final String enviroment = GeneralPreferences.getInstance().getEnviroment();
            if (StringUtil.isNotEmpty(serverUrl) && StringUtil.isNotEmpty(enviroment)) {
                GlobalSettings.BASE_URL = serverUrl;
                GlobalSettings.ENVIRONMENT = enviroment;
            } else {
                GeneralPreferences.getInstance().setServerUrl(GlobalSettings.BASE_URL);
                GeneralPreferences.getInstance().setEnviroment(GlobalSettings.ENVIRONMENT);
            }

             */
        }

        NotificationUtil.createLocationServiceNotificationChannel(getApplicationContext());
        NotificationUtil.createDefaultNotificationChannel(getApplicationContext());
        if (AuthUtil.isSingleUser(getApplicationContext())) {
            BasicUser basicUser = AuthUtil.getSingleUser(getApplicationContext());
            if (basicUser != null) {
                PreferencesManager.createInstance(getApplicationContext(), basicUser.getCredentials().login);
            }
        }
        initConstants();
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
    }

    /**
     * обработчик авторизации пользователя
     */
    public void onAuthorized(int type) {
        if (Authorization.getInstance() == null || Authorization.getInstance().user == null) {
            Toast.makeText(getApplicationContext(), getString(ru.mobnius.simple_core.R.string.critical_auth_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if (PreferencesManager.getInstance() == null) {
            PreferencesManager.createInstance(this, Authorization.getInstance().user.getCredentials().login);
        }
        if (PreferencesManager.getInstance() != null && PreferencesManager.getInstance().isNotCreated()) {
            PreferencesManager.createInstance(this, Authorization.getInstance().user.getCredentials().login);
        }

        long userId = -1;
        if (Authorization.getInstance() != null && Authorization.getInstance().user != null) {
            userId = Authorization.getInstance().user.getUserId();
        }
        final DaoSession daoSession = new DaoMaster(new DbOpenHelper(this, USER_ENG + userId + ".db").getWritableDb()).newSession();
        daoSession.clear();
        DataManager.createInstance(daoSession);
        if (PreferencesManager.getInstance() != null && PreferencesManager.getInstance().isDebug()) {
            QueryBuilder.LOG_SQL = true;
        }

        if (DataManager.getInstance() != null && Authorization.getInstance().user != null) {
            DataManager.getInstance().updateUser(userId,
                    Authorization.getInstance().user.getCredentials().login, StringUtil.EMPTY);
        }
        AuditUtil.writeAudit(AuditUtil.AUTHORIZED,
                AUTH_SUCCESS + StringUtil.SPACE + type,
                VersionUtil.getVersionName(getApplicationContext()));
    }

    /**
     * пользователь сбросил авторизацию
     *
     * @param clearUserAuthorization очистка авторизации пользователя
     */
    public void unAuthorized(boolean clearUserAuthorization) {
        AuditUtil.writeAudit(AuditUtil.UNAUTHORIZED, StringUtil.EMPTY, VersionUtil.getVersionName(getApplicationContext()));
        boolean zip = false;
        if (PreferencesManager.getInstance() != null) {
            zip = PreferencesManager.getInstance().getZip();
        }
        final ManualSynchronization manualSynchronization = ManualSynchronization.getInstance(VersionUtil.getVersionName(getApplicationContext()), zip);
        if (manualSynchronization != null) {
            manualSynchronization.destroy();
        }

        if (clearUserAuthorization && PreferencesManager.getInstance() != null) {
            PreferencesManager.getInstance().clear();
        }
        if (clearUserAuthorization) {
            if (PreferencesManager.getInstance() != null) {
                PreferencesManager.getInstance().setPinAuth(false);
            }
        }
        if (Authorization.getInstance() != null) {
            Authorization.getInstance().reset();
        }
        if (PreferencesManager.getInstance() != null) {
            PreferencesManager.getInstance().destroy();
        }

        if (DataManager.getInstance() != null) {
            DataManager.getInstance().destroy();
        }
    }

    public void clearUserData() {
        if (Authorization.getInstance() != null) {
            Authorization.getInstance().reset();
        }
        AuthUtil.clear(getApplicationContext(), true);
        if (PreferencesManager.getInstance() != null) {
            PreferencesManager.getInstance().clear();
            PreferencesManager.getInstance().destroy();
        }
        if (DataManager.getInstance() != null) {
            DataManager.getInstance().destroy();
        }
    }


    private void handleUncaughtException(final @NonNull Thread thread, final @NonNull Throwable e) {
        if (DataManager.getInstance() == null) {
            return;
        }
        e.printStackTrace();
        try {
            final long userId;
            String jbData = Build.MODEL + StringUtil.COLON + Build.ID + StringUtil.COLON + Build.MANUFACTURER;
            if (Authorization.getInstance() == null || Authorization.getInstance().user == null) {
                userId = LongUtil.MINUS;
                if (DataManager.getInstance().getAllRouteItems().size() > 0) {
                    jbData += StringUtil.COLON + DataManager.getInstance().getAllRouteItems().get(0).id;
                }
            } else {
                userId = Authorization.getInstance().user.getUserId();
            }
            final ClientErrors error = new ClientErrors();
            error.id = UUID.randomUUID().toString();
            error.fn_user = userId;
            error.dx_date = DateUtil.getNewDateStringForServer();
            error.c_message = getStackTrace(e);
            error.c_code = StringUtil.defaultEmptyString(e.getLocalizedMessage());
            error.c_version = VersionUtil.getVersionName(getApplicationContext());
            error.jb_data = jbData;
            error.d_created = error.dx_date;
            DataManager.getInstance().daoSession.getClientErrorsDao().insert(error);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        System.exit(1);
    }

    @NonNull
    public static String getStackTrace(final @NonNull Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    @NonNull
    public static AppComponent getComponent() {
        return appComponent;
    }

}
