package com.saucedemo.automation.utils;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowserUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(BrowserUtil.class);
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static ConfigReader configReader;

    public static void initializeBrowser() {
        configReader = new ConfigReader();
        WebDriver webDriver = DriverFactory.getDriver(configReader.getBrowser());
        webDriver.manage().window().maximize();
        driver.set(webDriver);
        logger.info("Browser initialized: {}", configReader.getBrowser());
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void navigateToBaseUrl() {
        if (configReader == null) {
            configReader = new ConfigReader();
        }
        String baseUrl = configReader.getBaseUrl();
        driver.get().get(baseUrl);
        logger.info("Navigated to: {}", baseUrl);
    }

    public static void closeBrowser() {
        if (driver.get() != null) {
            ScreenshotUtil.takeScreenshot(driver.get(), "test-complete");
            driver.get().quit();
            driver.remove();
            logger.info("Browser closed");
        }
    }
}
