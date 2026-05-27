package com.saucedemo.automation.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DriverFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);
    private static final String DRIVER_RESOURCE_PATH = "src/main/resources/drivers/";
    
    public static WebDriver getDriver(String browser) {
        WebDriver driver;

        switch (browser.toLowerCase()) {
            case "chrome": {
                setupChromeDriver();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                chromeOptions.addArguments("--disable-save-password-bubble");
                
                java.util.Map<String, Object> prefs = new java.util.HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                prefs.put("profile.password_manager_leak_detection", false);
                prefs.put("autofill.profile_enabled", false);
                prefs.put("profile.default_content_setting_values.notifications", 2);
                prefs.put("profile.default_content_settings.popups", 0);
                chromeOptions.setExperimentalOption("prefs", prefs);
                chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation", "enable-logging"});
                chromeOptions.setExperimentalOption("useAutomationExtension", false);
                
                driver = new ChromeDriver(chromeOptions);
                break;
            }

            case "firefox": {
                setupFirefoxDriver();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                
                firefoxOptions.addPreference("signon.rememberSignons", false);
                firefoxOptions.addPreference("signon.autofillForms", false);
                firefoxOptions.addPreference("dom.webnotifications.enabled", false);
                firefoxOptions.addPreference("geo.enabled", false);
                
                driver = new FirefoxDriver(firefoxOptions);
                break;
            }

            case "edge": {
                setupEdgeDriver();
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--start-maximized");
                edgeOptions.addArguments("--disable-notifications");
                
                java.util.Map<String, Object> prefs = new java.util.HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                prefs.put("autofill.profile_enabled", false);
                prefs.put("profile.default_content_setting_values.notifications", 2);
                edgeOptions.setExperimentalOption("prefs", prefs);
                edgeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation", "enable-logging"});
                
                driver = new EdgeDriver(edgeOptions);
                break;
            }

            case "chrome-headless": {
                setupChromeDriver();
                ChromeOptions headlessOptions = new ChromeOptions();
                headlessOptions.addArguments("--headless=new");
                headlessOptions.addArguments("--disable-gpu");
                headlessOptions.addArguments("--no-sandbox");
                headlessOptions.addArguments("--disable-dev-shm-usage");
                headlessOptions.addArguments("--window-size=1920,1080");
                headlessOptions.addArguments("--remote-allow-origins=*");
                headlessOptions.addArguments("--disable-blink-features=AutomationControlled");
                headlessOptions.addArguments("--disable-save-password-bubble");
                
                java.util.Map<String, Object> prefs = new java.util.HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                prefs.put("profile.password_manager_leak_detection", false);
                prefs.put("autofill.profile_enabled", false);
                prefs.put("profile.default_content_setting_values.notifications", 2);
                prefs.put("profile.default_content_settings.popups", 0);
                headlessOptions.setExperimentalOption("prefs", prefs);
                headlessOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation", "enable-logging"});
                headlessOptions.setExperimentalOption("useAutomationExtension", false);
                
                driver = new ChromeDriver(headlessOptions);
                break;
            }

            default:
                throw new IllegalArgumentException("Browser type not supported: " + browser);
        }

        return driver;
    }
    
    private static void setupChromeDriver() {
        String localDriver = findLocalDriver("chromedriver");
        if (localDriver != null) {
            System.setProperty("webdriver.chrome.driver", localDriver);
            logger.info("Using local ChromeDriver: {}", localDriver);
        } else {
            WebDriverManager.chromedriver().setup();
            logger.info("Using WebDriverManager for ChromeDriver");
        }
    }
    
    private static void setupFirefoxDriver() {
        String localDriver = findLocalDriver("geckodriver");
        if (localDriver != null) {
            System.setProperty("webdriver.gecko.driver", localDriver);
            logger.info("Using local GeckoDriver: {}", localDriver);
        } else {
            WebDriverManager.firefoxdriver().setup();
            logger.info("Using WebDriverManager for GeckoDriver");
        }
    }
    
    private static void setupEdgeDriver() {
        String localDriver = findLocalDriver("msedgedriver");
        if (localDriver != null) {
            System.setProperty("webdriver.edge.driver", localDriver);
            logger.info("Using local EdgeDriver: {}", localDriver);
        } else {
            WebDriverManager.edgedriver().setup();
            logger.info("Using WebDriverManager for EdgeDriver");
        }
    }
    
    private static String findLocalDriver(String driverName) {
        File driversDir = new File(DRIVER_RESOURCE_PATH);
        
        if (!driversDir.exists() || !driversDir.isDirectory()) {
            logger.debug("Drivers directory not found: {}", DRIVER_RESOURCE_PATH);
            return null;
        }
        
        File windowsDriver = new File(driversDir, driverName + ".exe");
        if (windowsDriver.exists() && windowsDriver.canExecute()) {
            return windowsDriver.getAbsolutePath();
        }
        
        File unixDriver = new File(driversDir, driverName);
        if (unixDriver.exists() && unixDriver.canExecute()) {
            return unixDriver.getAbsolutePath();
        }
        
        logger.debug("Local driver not found: {}", driverName);
        return null;
    }
}
