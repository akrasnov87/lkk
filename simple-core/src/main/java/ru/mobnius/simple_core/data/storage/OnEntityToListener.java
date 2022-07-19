package ru.mobnius.simple_core.data.storage;

public interface OnEntityToListener {
    String getObjectOperationType();
    boolean getIsDelete();
    boolean getIsSynchronization();
    String getTid();
    String getBlockTid();
}
