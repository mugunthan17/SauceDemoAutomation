package com.saucedemo.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class CheckoutPage {

    private WebDriver driver;

    @FindBy(id = "first-name")
    private WebElement firstNameField;

    @FindBy(id = "last-name")
    private WebElement lastNameField;

    @FindBy(id = "postal-code")
    private WebElement postalCodeField;

    @FindBy(id = "continue")
    private WebElement continueButton;

    @FindBy(id = "cancel")
    private WebElement cancelButton;

    @FindBy(id = "finish")
    private WebElement finishButton;

    @FindBy(className = "complete-header")
    private WebElement orderCompleteHeader;

    @FindBy(id = "back-to-products")
    private WebElement backHomeButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    @FindBy(className = "title")
    private WebElement pageTitle;

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void enterFirstName(String firstName) {
        BasePage.type(driver, firstNameField, firstName);
    }

    public void enterLastName(String lastName) {
        BasePage.type(driver, lastNameField, lastName);
    }

    public void enterPostalCode(String postalCode) {
        BasePage.type(driver, postalCodeField, postalCode);
    }

    public void clickContinue() {
        BasePage.click(driver, continueButton);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void clickCancel() {
        BasePage.click(driver, cancelButton);
    }

    public void clickFinish() {
        BasePage.click(driver, finishButton);
    }

    public boolean isOrderComplete() {
        try {
            BasePage.waitForVisible(driver, orderCompleteHeader);
            return orderCompleteHeader.getText().contains("Thank you for your order");
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorMessage() {
        BasePage.waitForVisible(driver, errorMessage);
        return errorMessage.getText();
    }

    public boolean isErrorMessageDisplayed() {
        try {
            BasePage.waitForVisible(driver, errorMessage);
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickBackToHome() {
        BasePage.click(driver, backHomeButton);
    }

    public boolean isCheckoutOverviewPage() {
        try {
            Thread.sleep(500);
            BasePage.waitForVisible(driver, pageTitle);
            String actualTitle = pageTitle.getText();
            return "Checkout: Overview".equals(actualTitle);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFinishButtonDisplayed() {
        try {
            List<WebElement> finishButtons = driver.findElements(By.id("finish"));
            if (finishButtons.isEmpty()) {
                return false;
            }
            BasePage.waitForVisible(driver, finishButtons.get(0));
            return finishButtons.get(0).isDisplayed() && finishButtons.get(0).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickFinishWithRetry() {
        try {
            List<WebElement> finishButtons = driver.findElements(By.id("finish"));
            if (finishButtons.isEmpty()) {
                throw new RuntimeException("Finish button not found in DOM");
            }
            
            WebElement button = finishButtons.get(0);
            BasePage.waitForVisible(driver, button);
            
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
            Thread.sleep(300);
            
            if (button.isEnabled()) {
                button.click();
            } else {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to click finish button: " + e.getMessage(), e);
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        try {
            return pageTitle.getText();
        } catch (Exception e) {
            return "Unable to get page title: " + e.getMessage();
        }
    }
}
