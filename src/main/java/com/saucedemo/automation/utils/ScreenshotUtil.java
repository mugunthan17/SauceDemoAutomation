package com.saucedemo.automation.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtil {

    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtil.class);

    public static String takeScreenshot(WebDriver driver, String filename) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String screenshotName = filename + "_" + timestamp + ".png";
        
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        File source = screenshot.getScreenshotAs(OutputType.FILE);
        
        String destination = System.getProperty("user.dir") + "/test-output/screenshots/" + screenshotName;
        File finalDestination = new File(destination);
        
        try {
            FileUtils.copyFile(source, finalDestination);
            logger.info("Screenshot saved: {}", destination);
        } catch (IOException e) {
            logger.error("Failed to save screenshot: {}", e.getMessage());
        }
        
        return destination;
    }

    public static String captureScreenshot(WebDriver driver) {
        return takeScreenshot(driver, "screenshot");
    }
}
