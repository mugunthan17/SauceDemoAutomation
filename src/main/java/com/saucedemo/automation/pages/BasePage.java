package com.saucedemo.automation.pages;

import com.saucedemo.automation.utils.WaitUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BasePage {

    public static void click(WebDriver driver, WebElement element) {
        WaitUtil.waitForElementToBeClickable(driver, element);
        element.click();
    }

    public static void type(WebDriver driver, WebElement element, String text) {
        WaitUtil.waitForElementToBeVisible(driver, element);
        element.clear();
        element.sendKeys(text);
    }

    public static void waitForVisible(WebDriver driver, WebElement element) {
        WaitUtil.waitForElementToBeVisible(driver, element);
    }

    public static void waitForClickable(WebDriver driver, WebElement element) {
        WaitUtil.waitForElementToBeClickable(driver, element);
    }
}
