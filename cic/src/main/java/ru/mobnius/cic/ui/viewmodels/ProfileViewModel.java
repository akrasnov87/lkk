package ru.mobnius.cic.ui.viewmodels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.mobnius.cic.concurent.LoadUserProfileTask;
import ru.mobnius.cic.concurent.MainTaskExecutor;
import ru.mobnius.cic.concurent.MainTaskExecutorCallback;
import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.ui.model.concurent.UserProfile;
import ru.mobnius.simple_core.utils.StringUtil;

public class ProfileViewModel extends ViewModel {
    @Nullable
    public MutableLiveData<UserProfile> userProfileLiveData;

    @NonNull
    public MutableLiveData<UserProfile> loadUserProfile() {
        if (userProfileLiveData == null) {
            userProfileLiveData = new MutableLiveData<>();
            loadUserProfileAsync();
        }
        return userProfileLiveData;
    }

    private void loadUserProfileAsync() {
        final MainTaskExecutor mainTaskExecutor = new MainTaskExecutor();
        final LoadUserProfileTask loadUserProfileTask = new LoadUserProfileTask();
        mainTaskExecutor.executeAsync(loadUserProfileTask, new MainTaskExecutorCallback<UserProfile>() {
            @Override
            public void onCallableComplete(@NonNull UserProfile result) {
                if (userProfileLiveData == null) {
                    return;
                }
                userProfileLiveData.postValue(result);
            }

            @Override
            public void onCallableError(@NonNull String error) {
                if (userProfileLiveData == null) {
                    return;
                }
                final UserProfile userProfile = new UserProfile();
                userProfile.isError = true;
                userProfile.error = error;
                userProfileLiveData.postValue(userProfile);
            }
        });
    }

    public String fio;

    public @NonNull
    String getTitleName() {
        if (DataManager.getInstance() == null) {
            return StringUtil.EMPTY;
        }
        if (StringUtil.isEmpty(fio)) {
            if (DataManager.getInstance().getProfile() == null) {
                return StringUtil.EMPTY;
            }
            fio = DataManager.getInstance().getProfile().fio;
        }
        return fio;
    }
}
