package ru.mobnius.simple_core.utils;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.AbstractDao;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Класс для обработки JSONObject и создания из него SQL запроса на добавление записи
 */
public class SqlInsertFromJSONObject {
    @NonNull
    public final String params;
    @NonNull
    public final String tableName;
    @NonNull
    public final String[] fields;

    public SqlInsertFromJSONObject(final @NonNull JSONObject object, final @NonNull String tableName, final @NonNull AbstractDao<?, ?> abstractDao) {
        this.tableName = tableName;

        final StringBuilder builder = new StringBuilder();
        final ArrayList<String> tempFields = new ArrayList<>();
        final Iterator<String> keys = object.keys();

        while (keys.hasNext()) {
            final String name = keys.next();
            if (isColumnExists(abstractDao, name.toLowerCase())) {
                builder.append("?,");
                tempFields.add(name);
            }
        }
        this.fields = tempFields.toArray(new String[0]);
        this.params = builder.substring(0, builder.length() - 1);
    }

    /**
     * Запрос в БД для вставки
     *
     * @param appendField добавить дополнительные поля
     * @return возвращается запрос
     */
    @NonNull
    public String convertToQuery(final boolean appendField) {
        final StringBuilder builder = new StringBuilder();
        for (final String field : fields) {
            builder.append(field).append(",");
        }
        String strAppendField = StringUtil.EMPTY;
        if (appendField) {
            strAppendField = ",OBJECT_OPERATION_TYPE,IS_DELETE,IS_SYNCHRONIZATION,TID,BLOCK_TID";
        }
        return "INSERT INTO " + tableName + "(" + builder.substring(0, builder.length() - 1) + strAppendField + ")" + " VALUES(" + params + (appendField ? ",?,?,?,?,?" : "") + ")";
    }

    /**
     * Получение объекта для передачи в запрос
     *
     * @param object      объект для обработки
     * @param appendField добавить дополнительные поля
     * @return Массив значений полей
     * @throws JSONException исключение
     */
    @NonNull
    public Object[] getValues(final @NonNull JSONObject object, final boolean appendField) throws JSONException {
        final Object[] values = new Object[appendField ? fields.length + 5 : fields.length];

        for (int i = 0; i < fields.length; i++) {
            values[i] = object.get(fields[i]);
        }
        if (appendField) {
            values[fields.length] = StringUtil.EMPTY;
            values[fields.length + 1] = false;
            values[fields.length + 2] = true;
            values[fields.length + 3] = StringUtil.EMPTY;
            values[fields.length + 4] = StringUtil.EMPTY;
        }
        return values;
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
