package ru.mobnius.cic.ui.viewmodels;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ru.mobnius.simple_core.data.authorization.AuthorizationMeta;
import ru.mobnius.simple_core.utils.VersionUtil;

public class AuthViewModelTest extends BaseModelTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    private final AuthViewModel viewModel = new AuthViewModel();

    @Before
    public void before() {
        viewModel.login = "iphone";
        viewModel.password = "1234";

    }

    @Test
    public void getAuthorizationDataOnline() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final MutableLiveData<AuthorizationMeta> liveData = viewModel.getAuthorizationDataOnline(VersionUtil.getVersionName(ApplicationProvider.getApplicationContext()));
        liveData.observeForever(userProfile -> latch.countDown());
        if (!latch.await(3, TimeUnit.SECONDS)) {
            throw new InterruptedException("CountDownLatch did not await");
        }
        Thread.sleep(1000);
        assertEquals(Objects.requireNonNull(liveData.getValue()).getUserId(), 69);
        viewModel.authorizationLiveData = null;
    }

    @Test
    public void getAuthorizationOffline() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final MutableLiveData<AuthorizationMeta> liveData = viewModel.getAuthorizationDataOnline(VersionUtil.getVersionName(ApplicationProvider.getApplicationContext()));
        liveData.observeForever(userProfile -> latch.countDown());
        latch.await(3, TimeUnit.SECONDS);
        Thread.sleep(1000);
        assertEquals(Objects.requireNonNull(liveData.getValue()).getUserId(), 69);
        viewModel.authorizationLiveData = null;
        viewModel.password = "";
        viewModel.getAuthorizationOffline(ApplicationProvider.getApplicationContext());
        assert viewModel.authorizationLiveData != null;
        assertEquals(Objects.requireNonNull(viewModel.authorizationLiveData.getValue()).message, "Логин или пароль введены не верно.");
        assertEquals(Objects.requireNonNull(viewModel.authorizationLiveData.getValue()).getUserId(), -1L);
        viewModel.authorizationLiveData = null;
        viewModel.password = "1234";
        viewModel.getAuthorizationOffline(ApplicationProvider.getApplicationContext());
        assertEquals(Objects.requireNonNull(viewModel.authorizationLiveData.getValue()).getUserId(), 69);
        viewModel.authorizationLiveData = null;
    }
}