package ru.mobnius.simple_core.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MetaTest {

    @Test
    public void isSuccessTest() {
        String successMessage = "OK";
        Meta meta = new Meta(Meta.OK, successMessage);
        assertTrue(meta.isSuccess());

        String failMessage = "FAIL";
        meta = new Meta(Meta.NOT_AUTHORIZATION, failMessage);

        assertFalse(meta.isSuccess());
        meta = new Meta(Meta.NOT_FOUND, failMessage);
        assertFalse(meta.isSuccess());

        meta = new Meta(Meta.ERROR_SERVER, failMessage);
        assertFalse(meta.isSuccess());
    }
}