package ru.mobnius.cic.ui.viewmodels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ru.mobnius.cic.CONSTS;

public class ProfileViewModelTest extends BaseModelTest {
    private final ProfileViewModel viewModel = new ProfileViewModel();

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void loadUserProfile() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        viewModel.loadUserProfile().observeForever(userProfile -> {
            latch.countDown();
        });
        if (!latch.await(3, TimeUnit.SECONDS)) {
            throw new InterruptedException("CountDownLatch did not await");
        }
        Thread.sleep(1000);
        assert viewModel.userProfileLiveData != null;
        assertEquals(Objects.requireNonNull(viewModel.userProfileLiveData.getValue()).cLogin, CONSTS.LOGIN);

    }

    @Test
    public void getTitleName() {
        assertNull(viewModel.fio);
        assertEquals(viewModel.getTitleName(), CONSTS.USER_FIO);
        assertEquals(viewModel.fio, CONSTS.USER_FIO);
    }
}