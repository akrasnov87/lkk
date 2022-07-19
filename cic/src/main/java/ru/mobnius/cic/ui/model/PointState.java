package ru.mobnius.cic.ui.model;

/**
 * Состояние задания
 */
public class PointState {

    /**
     * Было выполнено или нет
     */
    public boolean isDone;

    /**
     * Было синхронизировано или нет
     */
    public boolean isSync;

    /**
     * Было отклонено диспетчером или нет
     */
    public boolean isReject;

}
