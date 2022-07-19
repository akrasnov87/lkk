package ru.mobnius.simple_core.data.storage;

/**
 * Имена полей для запросов
 */
// TODO: 27/12/2019 потом убрать и перенести в Names
public interface DbConst {
    String BLOCK_TID = "BLOCK_TID";
    String TID = "TID";
    String ID = "id";
    String IS_SYNCHRONIZATION = "IS_SYNCHRONIZATION";
    String OBJECT_OPERATION_TYPE = "OBJECT_OPERATION_TYPE";
    String DELETE_FROM = "DELETE FROM ";
    String WHERE_TID_EQUAL = " where tid = ? ";

    String UPDATE_NOT_SUCCESS = "UPDATE %s SET IS_SYNCHRONIZATION = ?, TID = ?, BLOCK_TID  = ? WHERE TID  = ? AND BLOCK_TID = ?;";
    String UPDATE_SUCCESS = "UPDATE %s SET IS_SYNCHRONIZATION = ?, OBJECT_OPERATION_TYPE = ?, TID = ?, BLOCK_TID  = ? WHERE TID  = ? AND BLOCK_TID = ?;";

}
