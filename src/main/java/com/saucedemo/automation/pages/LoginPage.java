package com.saucedemo.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.saucedemo.automation.tests.LoginTests;

public class LoginPage {

    private WebDriver driver;
    private static final Logger logger = LoggerFactory.getLogger(LoginTests.class);

    @FindBy(id = "user-name")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void enterUsername(String username) {
        BasePage.type(driver, usernameField, username);
    }

    public void enterPassword(String password) {
        BasePage.type(driver, passwordField, password);
    }

    public void clickLoginButton() {
        BasePage.click(driver, loginButton);
    }

    public String getErrorMessage() {
        BasePage.waitForVisible(driver, errorMessage);
        return errorMessage.getText();
    }
    
    public boolean doLogin(String username, String password) {
    	boolean loggedIn = false;
    	logger.info("Step 1: Entering username: {}", username.isEmpty() ? "<empty>" : username);
    	enterUsername(username);
    	logger.info("Step 2: Entering password: {}", password.isEmpty() ? "<empty>" : "********");
    	enterPassword(password);
    	logger.info("Step 3: Clicking login button");
    	clickLoginButton();
    	loggedIn=true;
    	return loggedIn;
    }

    public boolean isErrorMessageDisplayed() {
        try {
            BasePage.waitForVisible(driver, errorMessage);
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
