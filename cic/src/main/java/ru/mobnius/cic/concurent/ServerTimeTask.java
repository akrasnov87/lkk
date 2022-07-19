package ru.mobnius.cic.concurent;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.ui.model.concurent.TimeRequestResult;
import ru.mobnius.simple_core.data.RequestManager;
import ru.mobnius.simple_core.data.authorization.Authorization;
import ru.mobnius.simple_core.data.rpc.DateRecord;
import ru.mobnius.simple_core.data.rpc.RPCResult;
import ru.mobnius.simple_core.data.rpc.ServerTimeRpcResult;
import ru.mobnius.simple_core.data.rpc.SingleItemQuery;
import ru.mobnius.simple_core.utils.DateUtil;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Класс проверки соответствия текущего времени на устройве времени на сервере
 * в дополнительном потоке
 */
public class ServerTimeTask implements Callable<TimeRequestResult> {
    private final static long FIVE_MIUTES_POSITIIVE = 5 * 60 * 1000;
    private final static long FIVE_MIUTES_NEGATIVE = -5 * 60 * 1000;

    @NonNull
    @Override
    public TimeRequestResult call() throws Exception {
        if (Authorization.getInstance() == null || Authorization.getInstance().user == null) {
            return new TimeRequestResult(StringUtil.EMPTY, false);
        }

        final ServerTimeRpcResult[] results = RequestManager.rpcServerTime(Authorization.getInstance().user.getCredentials().getToken(), new SingleItemQuery());
        if (results.length > 0 && results[0].result != null && results[0].result.records.length > 0) {
            final DateRecord dateRecord = results[0].result.records[0];
            final String dateString = dateRecord.date;
            final Date serverDate = DateUtil.getNullableDateFromServerString(dateString);
            if (serverDate == null) {
                return new TimeRequestResult(StringUtil.EMPTY, false);
            }
            final DateFormat dateFormatGmt = SimpleDateFormat.getDateTimeInstance();
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            final DateFormat dateFormatLocal = SimpleDateFormat.getDateTimeInstance();
            final Date currentDate = dateFormatLocal.parse(dateFormatGmt.format(new Date()));
            if (currentDate == null) {
                return new TimeRequestResult(StringUtil.EMPTY, false);
            }
            final long serverMillis = serverDate.getTime();
            final long currentMillis = currentDate.getTime();
            if (currentMillis - serverMillis < FIVE_MIUTES_POSITIIVE || currentMillis - serverMillis > FIVE_MIUTES_NEGATIVE) {
                return new TimeRequestResult(StringUtil.EMPTY, false);
            } else {
                return new TimeRequestResult(MobniusApplication.WRONG_TIMEZONE, true);
            }
        }
        return new TimeRequestResult(StringUtil.EMPTY, false);
    }

}
