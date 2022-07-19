package ru.mobnius.simple_core.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.DatabaseStatement;

import java.util.ArrayList;

/**
 * https://www.youtube.com/watch?v=E4zklaVBj5w&list=PLyfVjOYzujugap6Rf3ETNKkx4v9ePllNK&index=40&pbjreload=101
 */
public class SqlStatementInsertFromJSONObject {
    @NonNull
    public final String params;
    @NonNull
    public final String tableName;
    @NonNull
    public final String[] fields;
    @NonNull
    private final DatabaseStatement statement;
    private final boolean isRequestToServer;

    public SqlStatementInsertFromJSONObject(final @NonNull JsonObject object, final @NonNull String tableName,
                                            final boolean isRequestToServer, final @NonNull AbstractDao<?, ?> abstractDao) {
        this.tableName = tableName;
        this.isRequestToServer = isRequestToServer;

        final StringBuilder builder = new StringBuilder();
        final ArrayList<String> tempFields = new ArrayList<>();

        for (final String name : object.keySet()) {
            if (isColumnExists(abstractDao, name.toLowerCase())) {
                builder.append("?,");
                tempFields.add(name);
            }
        }
        this.fields = tempFields.toArray(new String[0]);
        this.params = builder.substring(0, builder.length() - 1);

        final String sql = convertToQuery(isRequestToServer);
        statement = abstractDao.getDatabase().compileStatement(sql);
    }

    /**
     * Получение объекта для передачи в запрос
     *
     * @param object объект для обработки
     */
    public void bind(final @NonNull JsonObject object) {
        statement.clearBindings();

        for (int i = 0; i < fields.length; i++) {
            bindObjectToStatement(statement, i + 1, object.get(fields[i]));
        }

        if (isRequestToServer) {
            statement.bindString(fields.length + 1, StringUtil.EMPTY);
            statement.bindLong(fields.length + 2, 0);
            statement.bindLong(fields.length + 3, 1);
            statement.bindString(fields.length + 4, StringUtil.EMPTY);
            statement.bindString(fields.length + 5, StringUtil.EMPTY);
        }
        statement.execute();
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
     * Колонка существует в текущей версии БД или нет
     *
     * @param columnName  имя колонки
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

    private void bindObjectToStatement(final @NonNull DatabaseStatement statement, final int index, final @Nullable JsonElement value) {
        //TODO это очень очень плохо
        if (value == null || value.isJsonNull()) {
            statement.bindNull(index);
        } else {
            final JsonPrimitive jsonPrimitive = value.getAsJsonPrimitive();

            if (jsonPrimitive.isNumber() && jsonPrimitive.getAsNumber().toString().contains(".")) {
                statement.bindDouble(index, value.getAsDouble());
            } else if (jsonPrimitive.isNumber()) {
                statement.bindLong(index, value.getAsLong());
            } else if (jsonPrimitive.isBoolean()) {
                statement.bindLong(index, value.getAsBoolean() ? 1 : 0);
            } else {
                statement.bindString(index, value.getAsString());
            }
        }
    }
}
