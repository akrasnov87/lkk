package ru.mobnius.simple_core.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {
    public static final String EMPTY = "{}";
    public static final String PATH_JSON_KEY = "path";
    public static final String ADDRESS_JSON_KEY = "c_address";
    public static final String TELEPHONE_JSON_KEY = "c_telephone";
    public static final String PERSON_JSON_KEY = "b_person";
    public static final String SUBSCR_JSON_KEY = "c_subscr";
    public static final String DEVICE_NUMBER_JSON_KEY = "device_number";
    public static final String DEVICE_TYPE_NAME_JSON_KEY = "device_type_name";
    public static final String OWNER_JSON_KEY = "owner_name";
    public static final String ACCOUNT_NUMBER_JSON_KEY = "number";
    public static final String DEVICE_MODEL_JSON_KEY = "c_device_model";
    public static final String DEVICE_INTERVAL_JSON_KEY = "c_device_interval";
    public static final String LATITUDE_JSON_KEY = "n_latitude";
    public static final String LONGITUDE_JSON_KEY = "n_longitude";
    public static final String TECHNICAL_METERING_JSON_KEY = "b_technical_metering";
    public static final String REGISTR_PTS_JSON_KEY = "c_registr_pts";
    public static final String METER_JSON_KEY = "meters";
    public static final String RECEIPT_JSON_KEY = "receipt";
    public static final String NOTIFICATION_ORP_JSON_KEY = "notification_orp";
    public static final String METER_ID_JSON_KEY = "id";
    public static final String METER_DATE_JSON_KEY = "date";
    public static final String METER_VALUE_JSON_KEY = "value";
    public static final String METER_DIGIT_RANK_JSON_KEY = "digits";
    public static final String VERSION_JSON_KEY = "version";
    public static final String META_JSON_KEY = "meta";
    public static final String DATA_JSON_KEY = "data";
    public static final String SUCCESS_JSON_KEY = "success";
    public static final String MSG_JSON_KEY = "msg";
    public static final String PROCESSED_JSON_KEY = "processed";
    public static final String NAME_JSON_KEY = "name";
    public static final String CODE_JSON_KEY = "code";
    public static final String ACTION_JSON_KEY = "action";
    public static final String METHOD_JSON_KEY = "method";
    public static final String TYPE_JSON_KEY = "type";
    public static final String TID_JSON_KEY = "tid";
    public static final String HOST_JSON_KEY = "host";
    public static final String RESULT_JSON_KEY = "result";
    public static final String TOTAL_JSON_KEY = "total";
    public static final String RECORDS_JSON_KEY = "records";
    public static final String START_JSON_KEY = "start";
    public static final String TOTAL_LENGTH_JSON_KEY = "totalLength";

    public static boolean isEmpty(String json) {
        return json.equals(EMPTY);
    }

    @NonNull
    public static String getNonNullString(final @NonNull JSONObject jsonObject, final @NonNull String name) {
        try {
            final String value = jsonObject.has(name) ? jsonObject.getString(name) : StringUtil.EMPTY;
            return StringUtil.defaultEmptyString(value);
        } catch (JSONException e) {
            return StringUtil.EMPTY;
        }
    }

    @Nullable
    public static String getNullableString(final @Nullable JSONObject jsonObject, final @NonNull String name) {
        if (jsonObject == null || !jsonObject.has(name)) {
            return null;
        }
        try {
            return jsonObject.getString(name);
        } catch (JSONException e) {
            return null;
        }
    }

    @NonNull
    public static String getNonNullDefaultString(final @NonNull JSONObject jsonObject,
                                                 final @NonNull String name,
                                                 final @NonNull String defaultString) {
        try {
            final String value = jsonObject.has(name) ? jsonObject.getString(name) : defaultString;
            return StringUtil.defaultEmptyString(value);
        } catch (JSONException e) {
            return defaultString;
        }
    }

    public static int getIntFromJSONObject(final @NonNull JSONObject jsonObject, final @NonNull String name, int defaultInt) {
        try {
            return jsonObject.has(name) ? jsonObject.getInt(name) : defaultInt;
        } catch (JSONException e) {
            return defaultInt;
        }
    }

    public static boolean getBooleanFromJSONObject(final @NonNull JSONObject jsonObject, final @NonNull String name, boolean defaultBoolean) {
        try {
            return jsonObject.has(name) ? jsonObject.getBoolean(name) : defaultBoolean;
        } catch (JSONException e) {
            return defaultBoolean;
        }
    }

    public static double getDoubleFromJSONObject(final @NonNull JSONObject jsonObject, final @NonNull String name, double defaultDouble) {
        try {
            return jsonObject.has(name) ? jsonObject.getDouble(name) : defaultDouble;
        } catch (JSONException e) {
            return defaultDouble;
        }
    }

    @Nullable
    public static Double getNullableDoubleFromJSONObject(final @NonNull JSONObject jsonObject, final @NonNull String name) {
        try {
            return jsonObject.has(name) ? jsonObject.getDouble(name) : null;
        } catch (JSONException e) {
            return null;
        }
    }

    @NonNull
    public static JSONObject getNonNullJsonObject(final @Nullable JSONObject jsonObject, @NonNull String name) {
        if (jsonObject == null || !jsonObject.has(name)) {
            return new JSONObject();
        }
        try {
            return jsonObject.getJSONObject(name);
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    @Nullable
    public static JSONObject getNullableJsonObject(final @Nullable JSONObject jsonObject, @NonNull String name) {
        if (jsonObject == null || !jsonObject.has(name)) {
            return null;
        }
        try {
            return jsonObject.getJSONObject(name);
        } catch (JSONException e) {
            return null;
        }
    }

    @Nullable
    public static Object getNullableObject(final @Nullable JSONObject jsonObject, @NonNull String name) {
        if (jsonObject == null || !jsonObject.has(name)) {
            return null;
        }
        try {
            return jsonObject.get(name);
        } catch (JSONException e) {
            return null;
        }
    }

    @NonNull
    public static JSONArray getNonNullJsonArray(final @Nullable JSONObject jsonObject, final @NonNull String jsonKey) {
        if (jsonObject == null || !jsonObject.has(jsonKey)) {
            return new JSONArray();
        }
        try {
            return jsonObject.getJSONArray(jsonKey);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }
}
