package ru.mobnius.cic.ui.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import ru.mobnius.simple_core.utils.DateUtil;
import ru.mobnius.simple_core.utils.DoubleUtil;
import ru.mobnius.simple_core.utils.JsonUtil;

/**
 * Класс представляющий счетчик электроэнергии
 */
public class MeterItem implements Serializable {

    /**
     * Максимально допустимая разрядность
     */
    public static final double DIGIT_RANK_DEFAULT = 13.6;

    /**
     * Дата текущих показаний
     */
    @Nullable
    public Date currentDate;

    /**
     * Дата предыдущих показаний, может быть null если отсутствует в БД
     */
    @Nullable
    public final Date previousDate;

    /**
     * Текущие показания
     */
    @Nullable
    private Double currentValue;


    /**
     * Текущие показания
     */
    public double startValue;


    /**
     * Текущие показания
     */
    @Nullable
    public String currentValueText;

    /**
     * Предыдущие показания, могут быть null если в отсутствуют в БД
     */
    @Nullable
    public final Double previousValue;

    /**
     * Разрядность прибора учета
     */
    public final double digitRank;

    /**
     * Наименование шкалы (Сутки, Ночь, День, Пик, Полупик)
     */
    @NonNull
    public final String name;

    /**
     * Идентификатор предыдущих покзаний в БД, может быть null
     */
    @Nullable
    public final String id;


    public MeterItem(final @Nullable String id,
                     final @NonNull String name,
                     final @Nullable Double previousValue,
                     final @Nullable Date previousDate,
                     final @Nullable Double currentValue,
                     final @Nullable Date currentDate,
                     final double digitRank) {
        this.id = id;
        this.currentDate = currentDate;
        this.previousDate = previousDate;
        this.digitRank = digitRank;
        this.currentValue = currentValue;
        this.previousValue = previousValue;
        this.name = name;
        this.currentValueText = getCurrentValueText();
        this.startValue = DoubleUtil.getDoubleOrZero(currentValue);
    }

    @NonNull
    public String getCurrentValueText() {
        return DoubleUtil.convertToText(currentValue);
    }

    public void setCurrentValue(final @Nullable Double currentValue) {
        this.currentValue = currentValue;
        this.currentValueText = getCurrentValueText();
    }

    @Nullable
    public Double getCurrentValue() {
        return this.currentValue;
    }

    @NonNull
    public String getPreviousValueText() {
        return DoubleUtil.convertToText(previousValue);
    }

    @NonNull
    public JSONObject getNewMeterJson() {
        JSONObject object = new JSONObject();
        if (currentValue == null) {
            return object;
        }
        try {
            object.put(JsonUtil.METER_ID_JSON_KEY, this.id);
            object.put(JsonUtil.METER_DATE_JSON_KEY, DateUtil.getDateStringForServer(this.currentDate));
            object.put(JsonUtil.METER_VALUE_JSON_KEY, String.valueOf(currentValue));
            return object;
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            return object;
        }
    }

    @SuppressWarnings("UnnecessaryBoxing")
    @NonNull
    public static MeterItem copy(final @NonNull MeterItem meterItem) {
        Double currentValue = null;
        if (meterItem.currentValue != null) {
            double value = meterItem.currentValue;
            currentValue = Double.valueOf(value);
        }

        return new MeterItem(meterItem.id, meterItem.name, meterItem.previousValue, meterItem.previousDate, currentValue, meterItem.currentDate, meterItem.digitRank);
    }
}
