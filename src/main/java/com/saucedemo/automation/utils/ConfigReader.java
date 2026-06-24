package com.saucedemo.automation.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private Properties properties;

    public ConfigReader() {
        properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/config/config.properties");
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            setDefaultProperties();
        }
    }

    private void setDefaultProperties() {
        properties.setProperty("browser", System.getProperty("browser", "chrome"));
        properties.setProperty("baseUrl", "https://www.saucedemo.com/");
        properties.setProperty("implicitWait", "10");
        properties.setProperty("explicitWait", "10");
    }

    public String getBrowser() {
        return System.getProperty("browser", properties.getProperty("browser", "chrome"));
    }

    public String getBaseUrl() {
        return properties.getProperty("baseUrl", "https://www.saucedemo.com/");
    }

    public int getImplicitWait() {
        return Integer.parseInt(properties.getProperty("implicitWait", "10"));
    }

    public int getExplicitWait() {
        return Integer.parseInt(properties.getProperty("explicitWait", "10"));
    }
}
