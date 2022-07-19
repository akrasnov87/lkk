package ru.mobnius.cic.ui.verification;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.mobnius.cic.Names;
import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.activity.MainActivity;
import ru.mobnius.cic.ui.component.CicAutocompleteFieldView;

public class CicAutocompleteVerificationTest {
    private ContextThemeWrapper context;

    @Before
    public void setUp(){
        context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.LkkBlueTheme);
    }

    @Test
    public void validateSolid() {
        new Handler(Looper.getMainLooper()).post(() -> {
        CicAutocompleteFieldView cicAutocompleteFieldView = new CicAutocompleteFieldView(context);
        CicAutocompleteFieldView.CicAutoCompleteFieldAdapter cicAutoCompleteFieldAdapter = new CicAutocompleteFieldView.CicAutoCompleteFieldAdapter(context);
            ArrayList<Map<String, Object>> items = new ArrayList<>();
            Map<String, Object> m = new HashMap<>();
            m.put(Names.ID, 0L);
            m.put(Names.NAME, "NAME");
            items.add(m);
            cicAutoCompleteFieldAdapter.updateItems(items);
            cicAutocompleteFieldView.setAdapter(cicAutoCompleteFieldAdapter);
            CicAutocompleteVerification cicAutocompleteVerification = new CicAutocompleteVerification(cicAutocompleteFieldView);
            assertFalse(cicAutocompleteVerification.validateSolid());
            cicAutocompleteFieldView.setFieldValue("0");
            assertTrue(cicAutocompleteVerification.validateSolid());
        });
    }
}