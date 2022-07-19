package ru.mobnius.cic.ui.verification;

import static org.junit.Assert.*;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.mobnius.cic.Names;
import ru.mobnius.cic.ui.activity.MainActivity;
import ru.mobnius.cic.ui.component.CicSpinner;

public class CicSpinnerVerificationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void validateSolid() {
        CicSpinner cicSpinner = new CicSpinner(ApplicationProvider.getApplicationContext());
        new Handler(Looper.getMainLooper()).post(() -> {
            ArrayList<Map<String, Object>> data = new ArrayList<>();
            Map<String, Object> m = new HashMap<>();
            m.put(Names.ID, 0L);
            m.put(Names.NAME, "NAME");
            data.add(m);
            cicSpinner.setData(data, false);
            CicSpinnerVerification cicSpinnerVerification = new CicSpinnerVerification(cicSpinner);
            assertFalse(cicSpinnerVerification.validateSolid());
            cicSpinner.setSelection(0);
            assertTrue(cicSpinnerVerification.validateSolid());
        });
    }
}