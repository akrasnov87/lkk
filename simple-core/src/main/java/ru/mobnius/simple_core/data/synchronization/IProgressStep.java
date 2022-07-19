package ru.mobnius.simple_core.data.synchronization;

/**
 * шаг выполнения синхронизации
 */
public interface IProgressStep {
    int NONE = 0;

    /**
     * начальная обработка
     */
    int START = 1;

    /**
     * формирование пакета
     */
    int PACKAGE_CREATE = 2;

    /**
     * Загрузка на сервер
     */
    int UPLOAD = 3;

    /**
     * Загрузка на клиент
     */
    int DOWNLOAD = 4;

    /**
     * восстановление
     */
    int RESTORE = 5;

    /**
     * завершение
     */
    int STOP = 6;

    /**
     * прогресс
     */
    int PERCENTAGE = 7;

    /**
     * конец трансфера
     */
    int END = 8;

    /**
     * ошибка
     */
    int ERROR = 9;

    /**
     * прогресс
     */
    int PROGRESS = 10;

    /**
     * конец трансфера
     */
    int FINISH = 11;
}
