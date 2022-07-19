package ru.mobnius.simple_core.data.synchronization.utils;

/**
 * Обработчик пакетов в оба направления с удалением существующих записей
 */
public class FullServerSidePackage extends ServerSidePackage {
    public FullServerSidePackage() {
        setDeleteRecordBeforeAppend(true);
    }
}
