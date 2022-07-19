package ru.mobnius.cic.ui.viewmodels;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.LinkedList;

import ru.mobnius.cic.concurent.MainTaskExecutor;
import ru.mobnius.cic.concurent.MainTaskExecutorCallback;
import ru.mobnius.cic.concurent.AuthTask;
import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.simple_core.data.Meta;
import ru.mobnius.simple_core.data.authorization.AuthorizationMeta;
import ru.mobnius.simple_core.data.credentials.BasicUser;
import ru.mobnius.simple_core.utils.AuthUtil;
import ru.mobnius.simple_core.utils.StringUtil;

public class AuthViewModel extends ViewModel {
    @Nullable
    public MutableLiveData<AuthorizationMeta> authorizationLiveData;
    @Nullable
    private MainTaskExecutor mainTaskExecutor;
    public String login;
    public String password;

    @NonNull
    public MutableLiveData<AuthorizationMeta> getAuthorizationDataOnline(final @NonNull String versionName) {
        if (mainTaskExecutor == null) {
            mainTaskExecutor = new MainTaskExecutor();
        } else {
            mainTaskExecutor.isRunning.set(false);
        }
        if (authorizationLiveData == null) {
            authorizationLiveData = new MutableLiveData<>();
            authorizeOnline(versionName);
        }
        return authorizationLiveData;

    }

    public void cancelAuth() {
        if (mainTaskExecutor != null) {
            mainTaskExecutor.isRunning.set(false);
        }
        if (authorizationLiveData != null) {
            authorizationLiveData = null;
        }
    }
    @NonNull
    public MutableLiveData<AuthorizationMeta> getAuthorizationOffline(final @NonNull Context context) {
        if (mainTaskExecutor == null) {
            mainTaskExecutor = new MainTaskExecutor();
        } else {
            mainTaskExecutor.isRunning.set(false);
        }
        if (authorizationLiveData == null) {
            authorizationLiveData = new MutableLiveData<>();
            authorizeOffline(context);
        }
        return authorizationLiveData;
    }

    private void authorizeOnline(final @NonNull String versionName) {
        if (authorizationLiveData == null) {
            authorizationLiveData = new MutableLiveData<>();
        }
        if (mainTaskExecutor == null) {
            mainTaskExecutor = new MainTaskExecutor();
        }
        if (StringUtil.isEmpty(login) || StringUtil.isEmpty(password)) {
            authorizationLiveData.setValue(new AuthorizationMeta(Meta.NOT_AUTHORIZATION, "Логин или пароль введены не верно.", null, null, null));
        }
        final AuthTask authTask = new AuthTask(login, password, versionName);
        mainTaskExecutor.executeAsync(authTask, new MainTaskExecutorCallback<AuthorizationMeta>() {
            @Override
            public void onCallableComplete(@NonNull AuthorizationMeta result) {
                authorizationLiveData.postValue(result);
            }

            @Override
            public void onCallableError(@NonNull String error) {
                authorizationLiveData.postValue(new AuthorizationMeta(Meta.NOT_AUTHORIZATION, error, null, null, null));
            }
        });
    }

    private void authorizeOffline(final @NonNull Context context) {
        if (authorizationLiveData == null) {
            authorizationLiveData = new MutableLiveData<>();
        }
        if (StringUtil.isEmpty(login) || StringUtil.isEmpty(password)) {
            authorizationLiveData.setValue(new AuthorizationMeta(Meta.NOT_AUTHORIZATION, "Логин или пароль введены не верно.", null, null, null));
        }
        final BasicUser basicUser = AuthUtil.readUser(context, login);
        if (basicUser == null) {
            authorizationLiveData.setValue(new AuthorizationMeta(Meta.NOT_AUTHORIZATION, "Необходимо выполнить онлайн авторизацию", null, null, null));
            return;
        }
        if (StringUtil.equals(basicUser.getCredentials().password, password)) {
            authorizationLiveData.setValue(new AuthorizationMeta(
                    Meta.OK,
                    MobniusApplication.AUTH_SUCCESS,
                    basicUser.getCredentials().getToken(),
                    basicUser.claims,
                    basicUser.getUserId()));
            return;
        }
        authorizationLiveData.setValue(new AuthorizationMeta(Meta.NOT_AUTHORIZATION, "Логин или пароль введены не верно.", null, null, null));
    }
}
