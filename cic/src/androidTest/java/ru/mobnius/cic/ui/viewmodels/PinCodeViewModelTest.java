package ru.mobnius.cic.ui.viewmodels;

import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;

public class PinCodeViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();


    @Test
    public void refreshPinCode() {
        final PinCodeViewModel viewModel = new PinCodeViewModel();
        viewModel.refreshPinCode("1");
        viewModel.refreshPinCode("1");
        assertEquals(viewModel.pinCode,  "11");
        viewModel.isCreate = true;
        viewModel.refreshPinCode("1");
        viewModel.refreshPinCode("1");
        assertEquals(viewModel.tempPin, "1111");
        assertEquals(viewModel.pinCode, "");

    }

    @Test
    public void clearOneDigit() {
        final PinCodeViewModel viewModel = new PinCodeViewModel();
        viewModel.refreshPinCode("1");
        viewModel.refreshPinCode("1");
        assertEquals(viewModel.pinCode,  "11");
        viewModel.clearOneDigit();
        assertEquals(viewModel.pinCode, "1");
    }
}