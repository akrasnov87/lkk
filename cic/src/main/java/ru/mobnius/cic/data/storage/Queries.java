package ru.mobnius.cic.data.storage;

public interface Queries {
    String POINT_ITEM_QUERY = "SELECT p.ID, " +
            " p.F_ROUTE, " +
            " (SELECT rr.ID FROM cd_results AS rr WHERE rr.FN_POINT = p.ID and rr.B_DISABLED = 0) AS RESULT_ID, " +
            " p.C_ADDRESS, " +
            " p.JB_DATA, " +
            " p.B_PERSON, " +
            " (SELECT COUNT() FROM cd_results AS rr WHERE rr.FN_POINT = p.ID and rr.B_DISABLED = 0) > 0 AS B_DONE, " +
            " (SELECT COUNT() FROM cd_results AS rr WHERE rr.FN_POINT = p.ID and rr.B_DISABLED = 0 and rr.IS_SYNCHRONIZATION) > 0 AS B_SYNC, " +
            " (SELECT COUNT() FROM cd_results AS rr WHERE rr.FN_POINT = p.ID and rr.B_DISABLED = 0 and rr.__B_REJECT = 1) > 0 as B_REJECT, " +
            " p.N_LATITUDE, " +
            " p.N_LONGITUDE, " +
            " p.N_ORDER " +
            " FROM cd_points AS p " +
            " INNER JOIN cd_routes AS r ON p.F_ROUTE = r.ID " +
            " WHERE p.F_ROUTE = ? " +
            " ORDER BY p.N_ORDER;";

    String POINT_COUNT_QUERY = "SELECT " +
            " (SELECT COUNT(*) FROM cd_points WHERE cd_points.f_route = ?) AS N_COUNT, " +
            " (SELECT COUNT(*) FROM cd_points as p " +
            " INNER JOIN cd_results AS r on p.ID = r.FN_POINT " +
            " WHERE p.F_ROUTE = ? AND r.__B_REJECT = 0 AND r.B_DISABLED = 0) AS N_DONE, " +
            " (SELECT COUNT(*) FROM cd_points AS p " +
            " INNER JOIN cd_results AS r on p.ID = r.FN_POINT " +
            " WHERE p.F_ROUTE = ? and r.IS_SYNCHRONIZATION = 1) AS N_SYNC;";

    String ROUTE_HISTORY_QUERY = "SELECT " +
            " rs.C_NAME,  rh.D_DATE " +
            " FROM cd_route_history AS rh " +
            " INNER JOIN cs_route_statuses AS rs ON rs.ID = rh.FN_STATUS " +
            " WHERE rh.FN_ROUTE = ?;";

    String POINT_STATE_QUERY = "SELECT " +
            " IS_SYNCHRONIZATION, __B_REJECT FROM cd_results AS r " +
            " WHERE r.FN_POINT = ? AND r.B_DISABLED = 0;";

    String RESUL_ID_QUERY = "SELECT ID FROM cd_results AS r " +
            "WHERE r.FN_POINT = ? AND r.B_DISABLED = 0;";

}
