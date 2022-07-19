package ru.mobnius.simple_core.data.synchronization;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * интерфейс механизма синхронизации
 */
public interface OnSynchronizationListeners {

    /**
     * Список сущностей
     *
     * @return возвращается список сущностей по которым разрешена отправка на сервер
     */
    @NonNull
    List<Entity> getEntityToList();

    /**
     * Список сущностей
     *
     * @param tid идентификатор транзакции
     * @return возвращается список сущностей с tid
     */
    @NonNull
    Entity[] getEntities(String tid);

    /**
     * Запуск на выполение
     *
     * @param progress результат выполнения
     */
    void start(final @NonNull IProgress progress);

    /**
     * Принудительная остановка выполнения
     */
    void stop();

    /**
     * обработчик ошибок
     *
     * @param step шаг см. IProgressStep
     * @param e    исключение
     * @param tid  идентификатор транзакции
     */
    void onError(final int step, final @NonNull Exception e, final @Nullable String tid);

    /**
     * обработчик ошибок
     *
     * @param step    шаг см. IProgressStep
     * @param message текстовое сообщение
     * @param tid     идентификатор транзакции
     */
    void onError(final int step, final @NonNull String message, final @Nullable String tid);

    void cancel();
}
