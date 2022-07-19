package ru.mobnius.simple_core.data.synchronization;

/**
 * Обратный вызов для синхронной отправки файлов на сервер,
 * решение проблемы неполной синхронизации
 */
public interface FileTransferStartCallback {
    void onFileTransferCanStart();
}
