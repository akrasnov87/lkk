package ru.mobnius.cic.ui.model.concurent;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.data.GlobalSettings;
import ru.mobnius.simple_core.data.RequestManager;
import ru.mobnius.simple_core.data.authorization.Authorization;
import ru.mobnius.simple_core.data.rpc.RPCResult;
import ru.mobnius.simple_core.utils.DoubleUtil;
import ru.mobnius.simple_core.utils.StringUtil;

public class UserProfile {
    @NonNull
    public String cLogin;
    @NonNull
    public String cFirstName;
    @NonNull
    public String cMiddleName;
    @NonNull
    public String cLastName;
    @NonNull
    public String cEmail;
    @NonNull
    public String cPhone;
    @NonNull
    public String cAddress;
    @NonNull
    public String imageUrl;
    @NonNull
    public String error;
    public double nRating;
    public boolean isError;

    public UserProfile() {
        this.cLogin = StringUtil.EMPTY;
        this.cFirstName = StringUtil.EMPTY;
        this.cMiddleName = StringUtil.EMPTY;
        this.cLastName = StringUtil.EMPTY;
        this.nRating = DoubleUtil.ZERO;
        this.cEmail = StringUtil.EMPTY;
        this.cAddress = StringUtil.EMPTY;
        this.cPhone = StringUtil.EMPTY;
        this.error = StringUtil.EMPTY;
        this.isError = false;
        this.imageUrl = StringUtil.EMPTY;
        if (Authorization.getInstance() != null && Authorization.getInstance().user != null) {
            this.imageUrl = GlobalSettings.getConnectUrl() + "/user/profile/image?" + RequestManager.AUTHORIZATION_HEADER + "=" + Authorization.getInstance().user.getCredentials().getToken();
        }
    }

    public void populate(final @NonNull RPCResult[] result) {
        if (result.length > 0 && result[0].result != null && result[0].result.records != null && result[0].result.records.length > 0) {
            final JsonObject object = result[0].result.records[0];
            this.cLogin = getStringFromJson(object, "c_login");
            this.cFirstName = getStringFromJson(object, "c_first_name");
            this.cMiddleName = getStringFromJson(object, "c_middle_name");
            this.cLastName = getStringFromJson(object, "c_last_name");
            this.cEmail = getStringFromJson(object, "c_email");
            this.cAddress = getStringFromJson(object, "c_address");
            this.cPhone = getStringFromJson(object, "c_phone");
            try {
                this.nRating = object.get("n_rating").getAsDouble();
            } catch (ClassCastException | IllegalStateException e) {
                e.printStackTrace();
            }
        } else {
            isError = true;
            error = BaseApp.SERVER_JSON_PARSE_ERROR;
        }
    }

    @NonNull
    private String getStringFromJson(final @NonNull JsonObject object, final @NonNull String key) {
        if (object.has(key)) {
            try {
                return object.get(key).getAsString();
            } catch (ClassCastException | IllegalStateException | UnsupportedOperationException e) {
                return StringUtil.EMPTY;
            }
        }
        return StringUtil.EMPTY;
    }
}
