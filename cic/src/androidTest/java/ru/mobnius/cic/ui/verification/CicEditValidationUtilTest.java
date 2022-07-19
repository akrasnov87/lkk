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

public class CicEditValidationUtilTest {
    private ContextThemeWrapper context;

    @Before
    public void setUp(){
        context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.LkkBlueTheme);
    }

    @Test
    public void validateFutureDate() {
        CicEditText cicEditText = new CicEditText(context);
        assertFalse(CicEditValidationUtil.validateFutureDate(null, cicEditText, false));
        assertFalse(CicEditValidationUtil.validateFutureDate("12/12/2020", cicEditText, false));
        assertFalse(CicEditValidationUtil.validateFutureDate("f", cicEditText, false));
        assertFalse(CicEditValidationUtil.validateFutureDate("12.12.2020", cicEditText, false));
        assertTrue(CicEditValidationUtil.validateFutureDate("12.12.2070", cicEditText, false));
    }

    @Test
    public void validatePastDate() {
        CicEditText cicEditText = new CicEditText(context);
        assertFalse(CicEditValidationUtil.validatePastDate(null, cicEditText, false));
        assertFalse(CicEditValidationUtil.validatePastDate("12/12/2020", cicEditText, false));
        assertFalse(CicEditValidationUtil.validatePastDate("f", cicEditText, false));
        assertFalse(CicEditValidationUtil.validatePastDate("12.12.2070", cicEditText, false));
        assertTrue(CicEditValidationUtil.validatePastDate("12.12.2020", cicEditText, false));
    }

    @Test
    public void validateYear() {
        CicEditText cicEditText = new CicEditText(context);
        assertFalse(CicEditValidationUtil.validateYear(null, cicEditText, false));
        assertFalse(CicEditValidationUtil.validateYear("f", cicEditText, false));
        assertFalse(CicEditValidationUtil.validateYear("11070", cicEditText, false));
        assertTrue(CicEditValidationUtil.validateYear("2020", cicEditText, false));
        assertTrue(CicEditValidationUtil.validateYear("2070", cicEditText, false));
    }
}