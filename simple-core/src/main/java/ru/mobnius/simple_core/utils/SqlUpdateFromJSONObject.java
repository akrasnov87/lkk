package ru.mobnius.simple_core.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.greenrobot.greendao.AbstractDao;

import java.util.ArrayList;
import java.util.Iterator;

import ru.mobnius.simple_core.data.storage.DbConst;

/**
 * Класс для обработки JSONObject и создания из него SQL запроса на обновление записи
 */
public class SqlUpdateFromJSONObject {
    @NonNull
    public final String params;
    @NonNull
    public final String tableName;
    @NonNull
    public final String[] fields;
    @NonNull
    public final String pkColumn;

    public SqlUpdateFromJSONObject(final @NonNull JsonObject object, final @NonNull String tableName,
                                   final @NonNull String pkColumn, final @NonNull AbstractDao<?, ?> abstractDao) {
        this.tableName = tableName;
        this.pkColumn = pkColumn;

        final StringBuilder builder = new StringBuilder();
        final ArrayList<String> tempFields = new ArrayList<>();
        final Iterator<String> keys = object.keySet().iterator();
        String fieldName;
        while (keys.hasNext()) {
            fieldName = keys.next();
            if (fieldName.equals(pkColumn)) {
                continue;
            }
            if (isColumnExists(abstractDao, fieldName.toLowerCase())) {
                tempFields.add(fieldName);
                builder.append(fieldName).append("  = ?, ");
            }
        }
        fields = tempFields.toArray(new String[0]);
        params = builder.substring(0, builder.length() - 2);
    }

    /**
     * Запрос в БД для обновления
     *
     * @param appendField добавить дополнительные поля
     * @return возвращается запрос
     */
    @NonNull
    public String convertToQuery(final boolean appendField) {
        String appendStr = StringUtil.EMPTY;
        if (appendField) {
            appendStr = " and (" + DbConst.OBJECT_OPERATION_TYPE + " = ? OR " + DbConst.OBJECT_OPERATION_TYPE + " = ?)";
        }
        return "UPDATE " + tableName + " set " + params + " where " + pkColumn + " = ?" + (appendField ? appendStr : "");
    }

    /**
     * Получение объекта для передачи в запрос
     *
     * @param object      объект для обработки
     * @param appendField добавить дополнительные поля
     * @return Массив значений полей
     */
    @NonNull
    public Object[] getValues(final @NonNull JsonObject object, final boolean appendField) {
        final ArrayList<Object> values = new ArrayList<>(fields.length);

        Object pk = null;

        for (final String field : fields) {
            if (pkColumn.equals(field)) {
                pk = toObject(object.get(field));
                continue;
            }
            values.add(toObject(object.get(field)));
        }

        values.add(pk);
        if (appendField) {
            values.add(null);
            values.add("");
        }

        return values.toArray();
    }

    @Nullable
    private Object toObject(final @Nullable JsonElement value) {
        if (value == null || value.isJsonNull()) {
            return null;
        } else if (value.getAsJsonPrimitive().isNumber()) {
            return value.getAsDouble();
        } else if (value.getAsJsonPrimitive().isBoolean()) {
            return value.getAsBoolean();
        } else {
            return value.getAsString();
        }
    }

    /**
     * Колонка существует в текущей версии БД или нет
     *
     * @param columnName имя колонки
     * @return true если колонка существует
     */
    private boolean isColumnExists(final @NonNull AbstractDao<?, ?> abstractDao, final @NonNull String columnName) {
        for (final String s : abstractDao.getAllColumns()) {
            if (StringUtil.equalsIgnoreCase(s, columnName)) {
                return true;
            }
        }
        return false;
    }
}