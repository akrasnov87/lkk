package ru.mobnius.cic.concurent;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;

import ru.mobnius.cic.ui.model.concurent.UserProfile;
import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.data.RequestManager;
import ru.mobnius.simple_core.data.authorization.Authorization;
import ru.mobnius.simple_core.data.rpc.RPCResult;

public class LoadUserProfileTask implements Callable<UserProfile> {
    @NonNull
    @Override
    public UserProfile call() throws Exception {
        final UserProfile userProfile = new UserProfile();
        if (Authorization.getInstance() == null || Authorization.getInstance().user == null) {
            userProfile.isError = true;
            userProfile.error = BaseApp.ERROR_NO_AUTHORIZATION_DATA;
            return userProfile;
        }
        final RPCResult[] result = RequestManager.getUserInfo(Authorization.getInstance().user.getCredentials().getToken());
        userProfile.populate(result);
        return userProfile;
    }
}
