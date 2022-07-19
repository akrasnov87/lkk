package ru.mobnius.cic.ui.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.mobnius.simple_core.utils.DateUtil;
import ru.mobnius.simple_core.utils.JsonUtil;
import ru.mobnius.simple_core.utils.StringUtil;

public class ResultItem {
    public static final String RESULT_STATUS_CREATED_CONST = "CREATED";

    @NonNull
    public final String id;
    @NonNull
    public final String date;
    @NonNull
    public String metersJson;
    public final boolean isSync;
    public boolean isCancelled;
    public int version;
    public boolean isReceipt;
    public boolean isNotificationOrp;

    public ResultItem(final @NonNull String id,
                      final @NonNull String date,
                      final @NonNull String jbData,
                      final boolean isSync,
                      final boolean isCancelled) {
        this.id = id;
        this.date = date;
        this.version = -1;
        this.metersJson = StringUtil.EMPTY;
        this.isSync = isSync;
        this.isCancelled = isCancelled;
        parseJson(jbData);
    }

    private void parseJson(final @Nullable String data) {
        if (StringUtil.isEmpty(data)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject metaObject = JsonUtil.getNonNullJsonObject(jsonObject, JsonUtil.META_JSON_KEY);
            version = JsonUtil.getIntFromJSONObject(metaObject, JsonUtil.VERSION_JSON_KEY, -1);
            JSONObject dataObject = JsonUtil.getNonNullJsonObject(jsonObject, JsonUtil.DATA_JSON_KEY);
            metersJson = JsonUtil.getNonNullString(dataObject, JsonUtil.METER_JSON_KEY);
            isReceipt = JsonUtil.getBooleanFromJSONObject(dataObject, JsonUtil.RECEIPT_JSON_KEY, false);
            isNotificationOrp = JsonUtil.getBooleanFromJSONObject(dataObject, JsonUtil.NOTIFICATION_ORP_JSON_KEY, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @NonNull
    public List<MeterItem> getResultMeters() {
        final List<MeterItem> meters = new ArrayList<>();
        try {
            final JSONArray array = new JSONArray(metersJson);
            for (int i = 0; i < array.length(); i++) {
                final JSONObject row = array.getJSONObject(i);
                final String id = JsonUtil.getNonNullString(row, JsonUtil.METER_ID_JSON_KEY);
                final Date date = DateUtil.getNonNullDateFromServerString(JsonUtil.getNonNullString(row, JsonUtil.METER_DATE_JSON_KEY));
                final Double value = JsonUtil.getNullableDoubleFromJSONObject(row, JsonUtil.METER_VALUE_JSON_KEY);
                final MeterItem meterItem = new MeterItem(id, StringUtil.EMPTY, null, null, value, date, MeterItem.DIGIT_RANK_DEFAULT);
                meters.add(meterItem);
            }
            return meters;
        } catch (JSONException e) {
            e.printStackTrace();
            return meters;
        }
    }


}
