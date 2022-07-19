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

import java.util.Date;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.activity.MainActivity;
import ru.mobnius.cic.ui.component.CicEditText;

public class MeterItemDigitVerificationTest {
    private ContextThemeWrapper context;


    private CicEditText editText;
    private MeterDigitVerification meterDigitVerification;


    @Before
    public void setUp(){
            context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.LkkBlueTheme);
        editText = new CicEditText(context);
    }

    @Test
    public void testValidateSoftMethod() {

        meterDigitVerification = new MeterDigitVerification(editText, 0, 5.1, 13000.0, new Date(System.currentTimeMillis() - 35L * 24 * 60 * 60 * 1000));
        editText.setText("");
        assertFalse(meterDigitVerification.validateSoft());
        editText.setText("abs");
        assertFalse(meterDigitVerification.validateSoft());
        editText.setText("12000");
        assertFalse(meterDigitVerification.validateSoft());
        editText.setText("22000");
        assertFalse(meterDigitVerification.validateSoft());
        editText.setText("13100");
        assertTrue(meterDigitVerification.validateSoft());
    }

    @Test
    public void testValidateSolidMethod() {

        meterDigitVerification = new MeterDigitVerification(editText, 0, null, null, null);
        assertFalse(meterDigitVerification.validateSolid());
        editText.setText("f");
        assertFalse(meterDigitVerification.validateSolid());
        editText.setText("1.f");
        assertFalse(meterDigitVerification.validateSolid());
        editText.setText("12345678.1234567");
        assertFalse(meterDigitVerification.validateSolid());
        editText.setText("12345678.123456");
        assertTrue(meterDigitVerification.validateSolid());

    }

}