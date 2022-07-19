package ru.mobnius.simple_core.data.synchronization.utils;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@RunWith(AndroidJUnit4.class)
public class SocketStatusReaderTest {
    @Test
    public void getInstanceTest(){
        SocketStatusReader reader = SocketStatusReader.getInstance("[DONE]12;hello world;12568");
        assertEquals(Objects.requireNonNull(reader).name, "DONE");
        assertEquals(reader.params.length, 3);
        Object[] params = reader.params;
        assertEquals(params[0], "12");
        assertEquals(params[1], "hello world");
        assertEquals(params[2], "12568");

        reader = SocketStatusReader.getInstance("test");
        assertNull(reader);
    }
}