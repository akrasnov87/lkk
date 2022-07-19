package ru.mobnius.simple_core.data.configuration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ru.mobnius.simple_core.utils.StringUtil;

public class ConfigurationSettingUtilTest {

    @Test
    public void getIntegerValue() {
        ConfigurationSetting configurationSetting = new ConfigurationSetting();
        configurationSetting.key = "int";
        configurationSetting.value = "10";
        configurationSetting.type = ConfigurationSetting.INTEGER;

        int integer = ConfigurationSettingUtil.getIntegerValue(configurationSetting, 2);
        assertEquals(integer, 10);

        configurationSetting = new ConfigurationSetting();
        configurationSetting.key = "int";
        configurationSetting.value = "test";
        configurationSetting.type = ConfigurationSetting.INTEGER;
        integer = ConfigurationSettingUtil.getIntegerValue(configurationSetting, 5);
        assertEquals(integer, 5);
    }

    @Test
    public void getDoubleValue() {
        ConfigurationSetting configurationSetting = new ConfigurationSetting("real", "2.52", "label real", "summary real", ConfigurationSetting.REAL);
        double aDouble = ConfigurationSettingUtil.getDoubleValue(configurationSetting, 0d);
        assertEquals(aDouble, 2.52, 0);

        configurationSetting.value = "test";
        aDouble = ConfigurationSettingUtil.getDoubleValue(configurationSetting, 14d);
        assertEquals(14d, aDouble, 0.1);
    }

    @Test
    public void getStringValue() {
        ConfigurationSetting configurationSetting = new ConfigurationSetting("text", "Hello", ConfigurationSetting.TEXT);
        String s = ConfigurationSettingUtil.getStringValue(configurationSetting);

        assertNotNull(s);
        assertEquals(s, "Hello");

        configurationSetting.value = null;
        s = ConfigurationSettingUtil.getStringValue(configurationSetting);
        assertEquals(s, StringUtil.EMPTY);
    }

    @Test
    public void getBooleanValue() {
        ConfigurationSetting configurationSetting = new ConfigurationSetting("boolean", "1", ConfigurationSetting.BOOLEAN);
        boolean aBoolean = ConfigurationSettingUtil.getBooleanValue(configurationSetting, false);

        assertTrue(aBoolean);

        configurationSetting.value = "0";
        aBoolean = ConfigurationSettingUtil.getBooleanValue(configurationSetting, true);
        assertFalse(aBoolean);

        configurationSetting.value = "5";
        aBoolean = ConfigurationSettingUtil.getBooleanValue(configurationSetting, false);
        assertFalse(aBoolean);
    }
}