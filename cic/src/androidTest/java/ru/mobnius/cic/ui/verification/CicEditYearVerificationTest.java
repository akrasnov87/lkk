package ru.mobnius.cic.ui.verification;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

import android.content.Context;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.activity.MainActivity;
import ru.mobnius.cic.ui.component.CicEditText;

public class CicEditYearVerificationTest {
    private ContextThemeWrapper context;

    @Before
    public void setUp(){
        context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.LkkBlueTheme);
    }

    @Test
    public void validateSolid() {
        CicEditText cicEditText = new CicEditText(context);
        CicEditYearVerification cicEditYearVerification = new CicEditYearVerification(cicEditText, CicEditYearVerification.FUTUTRE);
        assertFalse(cicEditYearVerification.validateSolid());
        cicEditText.setText("f");
        assertFalse(cicEditYearVerification.validateSolid());
        cicEditText.setText("123");
        assertFalse(cicEditYearVerification.validateSolid());
        cicEditText.setText("2020");
        assertFalse(cicEditYearVerification.validateSolid());
        cicEditText.setText("2022");
        assertTrue(cicEditYearVerification.validateSolid());
    }
}