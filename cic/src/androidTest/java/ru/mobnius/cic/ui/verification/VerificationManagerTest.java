package ru.mobnius.cic.ui.verification;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.widget.NestedScrollView;

import org.junit.Before;
import org.junit.Test;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.component.CicEditText;

public class VerificationManagerTest {
    private CicEditText cicEditText;
    private VerifiableField<CicEditText> field1;
    private VerifiableField<CicEditText> field2;
    private VerifiableField<CicEditText> field3;
    private ContextThemeWrapper context;
    @Before
    public void setUp() throws Exception {
        context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.LkkBlueTheme);
        cicEditText = new CicEditText(context);
        field1 = new VerifiableField<CicEditText>() {
            @NonNull
            @Override
            public CicEditText getView() {
                return cicEditText;
            }

            @Override
            public int getValidSoftType() {
                return 0;
            }

            @Override
            public void setOrder(int order) {

            }

            @Override
            public int getOrder() {
                return 0;
            }

            @Override
            public boolean validateSoft() {
                return true;
            }

            @Override
            public boolean validateSolid() {
                return false;
            }

            @Override
            public boolean isClearable() {
                return false;
            }

            @Override
            public boolean validateInvisible() {
                return false;
            }
        };
        field2 = new VerifiableField<CicEditText>() {
            @NonNull
            @Override
            public CicEditText getView() {
                return cicEditText;
            }

            @Override
            public int getValidSoftType() {
                return VerifiableField.CAN_SAVE_AFTER_MOUNT_AVG_MESSAGE;
            }

            @Override
            public void setOrder(int order) {

            }

            @Override
            public int getOrder() {
                return 1;
            }

            @Override
            public boolean validateSoft() {
                return false;
            }

            @Override
            public boolean validateSolid() {
                return true;
            }

            @Override
            public boolean isClearable() {
                return false;
            }

            @Override
            public boolean validateInvisible() {
                return false;
            }
        };
        field3 = new VerifiableField<CicEditText>() {
            @NonNull
            @Override
            public CicEditText getView() {
                return cicEditText;
            }

            @Override
            public int getValidSoftType() {
                return 0;
            }

            @Override
            public void setOrder(int order) {

            }

            @Override
            public int getOrder() {
                return 2;
            }

            @Override
            public boolean validateSoft() {
                return true;
            }

            @Override
            public boolean validateSolid() {
                return true;
            }

            @Override
            public boolean isClearable() {
                return false;
            }

            @Override
            public boolean validateInvisible() {
                return false;
            }
        };
    }

    @Test
    public void addFieldToBeVerified() {
        VerificationManager verificationManager = new VerificationManager();
        verificationManager.addFieldToBeVerified(field1, 0);
        verificationManager.addFieldToBeVerified(field2, 1);
        assertEquals(2, verificationManager.getVerifiableFieldsListSize());
        verificationManager.addFieldToBeVerified(field3, 1);
        assertEquals(2, verificationManager.getVerifiableFieldsListSize());
        assertEquals(field1, verificationManager.getVerifiableFieldsList().get(0));
        assertEquals(field3, verificationManager.getVerifiableFieldsList().get(1));
    }

    @Test
    public void removeFieldByOrder() {
        VerificationManager verificationManager = new VerificationManager();
        verificationManager.addFieldToBeVerified(field1, 0);
        verificationManager.addFieldToBeVerified(field2, 1);
        verificationManager.addFieldToBeVerified(field3, 2);
        verificationManager.removeFieldByOrder(5);
        assertEquals(3, verificationManager.getVerifiableFieldsListSize());
        verificationManager.removeFieldByOrder(0);
        assertEquals(field2, verificationManager.getVerifiableFieldsList().get(0));
        assertEquals(field3, verificationManager.getVerifiableFieldsList().get(1));
    }

    @Test
    public void verify() {
        NestedScrollView view = new NestedScrollView(context);
        VerificationManager verificationManager = new VerificationManager();
        verificationManager.addFieldToBeVerified(field1, 0);
        assertEquals(VerifiableField.CAN_NOT_SAVE, verificationManager.verify(view, false));
        verificationManager.addFieldToBeVerified(field2, 0);
        assertEquals(VerifiableField.CAN_SAVE_AFTER_MOUNT_AVG_MESSAGE, verificationManager.verify(view, false));
        verificationManager.addFieldToBeVerified(field3, 0);
        assertEquals(VerifiableField.CAN_SAVE, verificationManager.verify(view, false));
    }

}