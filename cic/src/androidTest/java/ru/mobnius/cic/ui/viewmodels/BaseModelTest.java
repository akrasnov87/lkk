package ru.mobnius.cic.ui.viewmodels;

import android.graphics.Bitmap;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import ru.mobnius.cic.CONSTS;
import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.data.storage.DbOpenHelper;
import ru.mobnius.cic.data.storage.models.AttachmentTypes;
import ru.mobnius.cic.data.storage.models.Attachments;
import ru.mobnius.cic.data.storage.models.Causes;
import ru.mobnius.cic.data.storage.models.DaoMaster;
import ru.mobnius.cic.data.storage.models.DaoSession;
import ru.mobnius.cic.data.storage.models.Points;
import ru.mobnius.cic.data.storage.models.Results;
import ru.mobnius.cic.data.storage.models.RouteStatuses;
import ru.mobnius.cic.data.storage.models.Routes;
import ru.mobnius.cic.data.storage.models.Users;
import ru.mobnius.simple_core.data.authorization.Authorization;
import ru.mobnius.simple_core.utils.LongUtil;

public class BaseModelTest {

    private File device;
    private File seal;
    private File meter;

    @Before
    public void setUp() throws Exception {
        ApplicationProvider.getApplicationContext().deleteDatabase("user69.db");
        final DaoSession daoSession = new DaoMaster(new DbOpenHelper(ApplicationProvider.getApplicationContext(), "user69.db").getWritableDb()).newSession();
        daoSession.clear();
        DataManager.createInstance(daoSession);
        Authorization.createTest();
        if (DataManager.getInstance() == null) {
            throw new NullPointerException("DataManager is null");
        }
        final Users user = new Users();
        user.id = CONSTS.TEST_USER_ID;
        user.c_fio = CONSTS.USER_FIO;
        user.c_login = CONSTS.LOGIN;
        user.f_parent = LongUtil.MINUS;
        user.c_email = CONSTS.EMAIL;
        DataManager.getInstance().daoSession.getUsersDao().insert(user);

        final Routes route = new Routes();
        route.id = CONSTS.ROUTE_ID;
        route.c_name = CONSTS.ROUTE_NAME;
        route.d_date_start = CONSTS.D_DATE;
        route.d_date_end = CONSTS.D_DATE;
        DataManager.getInstance().daoSession.getRoutesDao().insert(route);

        final Points point = new Points();
        point.id = CONSTS.POINT_ID;
        point.f_route = CONSTS.ROUTE_ID;
        point.c_owner = CONSTS.OWNER;
        point.c_subscr = CONSTS.C_SUBSCR;
        point.c_address = CONSTS.C_ADDRESS;
        point.jb_data = CONSTS.POINT_JB_DATA;
        DataManager.getInstance().daoSession.getPointsDao().insert(point);


        final Results result = new Results();
        result.id = CONSTS.RESULT_ID;
        result.c_notice = CONSTS.C_NOTICE;
        result.d_date = CONSTS.D_DATE;
        result.fn_user = CONSTS.TEST_USER_ID;
        result.n_latitude = CONSTS.N_LATITUDE;
        result.n_longitude = CONSTS.N_LONGITUDE;
        result.jb_data = CONSTS.RESULT_JB_DATA;
        result.fn_point = CONSTS.POINT_ID;
        result.fn_route = CONSTS.ROUTE_ID;
        DataManager.getInstance().daoSession.getResultsDao().insert(result);

        for (long i = 3L; i <= 14L; i++) {
            if (i == 6L || i == 7L) {
                continue;
            }
            final String causeConst = getCauseConst(i);
            final Causes cause = new Causes();
            cause.id = i;
            cause.c_const = causeConst;
            cause.c_name = CONSTS.CAUSE_NAME;
            cause.b_mobile = true;
            DataManager.getInstance().daoSession.getCausesDao().insert(cause);
        }
        for (long i = 1L; i <= 13L; i++) {
            if (i == 2L || i == 4L) {
                continue;
            }
            final String routeStatusesConst = getRouteStatusesConst(i);
            final RouteStatuses routeStatus = new RouteStatuses();
            routeStatus.id = i;
            routeStatus.c_const = routeStatusesConst;
            routeStatus.c_name = "some name";
            DataManager.getInstance().daoSession.getRouteStatusesDao().insert(routeStatus);
            final List<RouteStatuses> routeStatuses = DataManager.getInstance().daoSession.getRouteStatusesDao().loadAll();
            if (routeStatuses.size() == 0){
                throw new IllegalArgumentException("RouteStatus does not exist");
            }
        }
        for (long i = 1L; i <= 5L; i++) {
            final String attachmentConst = getAttachmentTypeConst(i);
            final AttachmentTypes attachmentType = new AttachmentTypes();
            attachmentType.id = i;
            attachmentType.c_const = attachmentConst;
            attachmentType.c_name = CONSTS.ATACHMENT_TYPE_NAME;
            DataManager.getInstance().daoSession.getAttachmentTypesDao().insert(attachmentType);
        }
        final File dir = ApplicationProvider.getApplicationContext().getFilesDir();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new FileNotFoundException("Can not create dir");
            }
        }
        final String deviceFileName = "device.jpg";
        final String sealFileName = "seal.jpg";
        final String meterFileName = "meter.jpg";
        device = new File(dir, deviceFileName);
        seal = new File(dir, sealFileName);
        meter = new File(dir, meterFileName);
        final Bitmap.Config config = Bitmap.Config.ARGB_8888;
        final Bitmap emptyBitmap = Bitmap.createBitmap(1, 1, config);
        try (FileOutputStream out = new FileOutputStream(device)) {
            emptyBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileOutputStream out = new FileOutputStream(seal)) {
            emptyBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileOutputStream out = new FileOutputStream(meter)) {
            emptyBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Attachments attachmentDevice = new Attachments();
        attachmentDevice.fn_point = CONSTS.POINT_ID;
        attachmentDevice.fn_route = CONSTS.ROUTE_ID;
        attachmentDevice.fn_result = CONSTS.RESULT_ID;
        attachmentDevice.fn_user = CONSTS.TEST_USER_ID;
        attachmentDevice.fn_type = 1L;
        attachmentDevice.n_size = 5000;
        attachmentDevice.c_mime = "image/jpeg";
        attachmentDevice.c_path = deviceFileName;
        attachmentDevice.c_real_file_path = device.getAbsolutePath();
        final Attachments attachmentSeal = new Attachments();
        attachmentSeal.fn_point = CONSTS.POINT_ID;
        attachmentSeal.fn_route = CONSTS.ROUTE_ID;
        attachmentSeal.fn_result = CONSTS.RESULT_ID;
        attachmentSeal.fn_user = CONSTS.TEST_USER_ID;
        attachmentSeal.fn_type = 2L;
        attachmentSeal.n_size = 5000;
        attachmentSeal.c_mime = "image/jpeg";
        attachmentSeal.c_path = sealFileName;
        attachmentSeal.c_real_file_path = seal.getAbsolutePath();
        final Attachments attachmentMeter = new Attachments();
        attachmentMeter.fn_point = CONSTS.POINT_ID;
        attachmentMeter.fn_route = CONSTS.ROUTE_ID;
        attachmentMeter.fn_result = CONSTS.RESULT_ID;
        attachmentMeter.fn_user = CONSTS.TEST_USER_ID;
        attachmentMeter.fn_type = 3L;
        attachmentMeter.n_size = 5000;
        attachmentMeter.c_mime = "image/jpeg";
        attachmentMeter.c_path = meterFileName;
        attachmentMeter.c_real_file_path = meter.getAbsolutePath();
        DataManager.getInstance().daoSession.getAttachmentsDao().insert(attachmentDevice);
        DataManager.getInstance().daoSession.getAttachmentsDao().insert(attachmentSeal);
        DataManager.getInstance().daoSession.getAttachmentsDao().insert(attachmentMeter);

    }

    @After
    public void tearDown() throws FileNotFoundException {
        MobniusApplication m = ApplicationProvider.getApplicationContext();
        m.unAuthorized(true);
        ApplicationProvider.getApplicationContext().deleteDatabase("user69.db");
        if (device == null || meter == null || seal == null) {
            return;
        }
        if (!device.delete() || !meter.delete() || !seal.delete()) {
            throw new FileNotFoundException("Can not delete file photo.jpg");
        }

    }

    private String getCauseConst(final long causeId) {
        switch ((int) causeId) {
            case 3:
                return "NO_ACCESS";

            case 4:
                return "MECHANICAL_DAMAGE";

            case 5:
                return "CONSUMER_BLOCK";

            case 8:
                return "DEVICE_REPLACE";

            case 9:
                return "SEAL_DAMAGE";

            case 10:
                return "TYPE_MISSMATCH";

            case 11:
                return "NUMBER_MISSMATCH";

            case 12:
                return "LIQUIDATED";

            case 13:
                return "DEVICE_MISSING";
            case 14:
                return "DEVICE_DISCONNECTED";
            default:
                return "";
        }
    }

    private String getRouteStatusesConst(final long routeStatusId) {
        switch ((int) routeStatusId) {
            case 1:
                return "IMPORT";
            case 3:
                return "CREATED";
            case 5:
                return "PROCCESS";
            case 6:
                return "DONED";
            case 7:
                return "EXPIRED";
            case 8:
                return "CONFIRMED";
            case 9:
                return "NOT_CONFIRMED";
            case 10:
                return "CANCEL";
            case 11:
                return "EXPORT";
            case 12:
                return "ASSINGNED";
            case 13:
                return "RECEIVED";
            default:
                return "";
        }
    }

    private String getAttachmentTypeConst(final long attachmentTypeId) {
        switch ((int) attachmentTypeId) {
            case 1:
                return "DEVICE";

            case 2:
                return "SEAL";

            case 3:
                return "METER";

            case 4:
                return "OVERVIEW";

            case 5:
                return "FAILURE";
            default:
                return "";
        }
    }
}
