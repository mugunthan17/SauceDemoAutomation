package com.saucedemo.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InventoryPage {

    private WebDriver driver;
    private static final Logger logger = LoggerFactory.getLogger(InventoryPage.class);

    @FindBy(className = "title")
    private WebElement pageTitle;

    @FindBy(className = "inventory_item")
    private List<WebElement> inventoryItems;

    @FindBy(css = ".inventory_item button")
    private List<WebElement> addToCartButtons;

    @FindBy(className = "shopping_cart_link")
    private WebElement shoppingCartLink;

    @FindBy(className = "shopping_cart_badge")
    private WebElement cartBadge;

    @FindBy(id = "react-burger-menu-btn")
    private WebElement menuButton;

    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isInventoryPageDisplayed() {
        try {
            BasePage.waitForVisible(driver, pageTitle);
            return pageTitle.getText().equals("Products");
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForInventoryItemsToLoad() {
        try {
            Thread.sleep(500);
            List<WebElement> items = driver.findElements(By.className("inventory_item"));
            logger.info("Inventory page loaded with {} items", items.size());
            
            if (items.isEmpty()) {
                throw new RuntimeException("No inventory items found on page");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Wait interrupted: {}", e.getMessage());
        }
    }

    public void addItemToCart(int itemIndex) {
        try {
            if (!isInventoryPageDisplayed()) {
                logger.error("Not on inventory page! Current URL: {}", driver.getCurrentUrl());
                throw new RuntimeException("Not on inventory page. Cannot add items to cart.");
            }
            
            List<WebElement> buttons = driver.findElements(By.cssSelector(".inventory_item button"));
            
            logger.info("Found {} add-to-cart buttons on inventory page", buttons.size());
            
            if (buttons.isEmpty()) {
                throw new RuntimeException("No add-to-cart buttons found on inventory page");
            }
            
            if (itemIndex >= buttons.size()) {
                throw new RuntimeException("Invalid product index: " + itemIndex + ". Only " + buttons.size() + " products available");
            }
            
            WebElement button = buttons.get(itemIndex);
            String buttonTextBefore = button.getText();
            logger.info("Attempting to click button at index {} with text: '{}'", itemIndex, buttonTextBefore);
            
            BasePage.click(driver, button);
            
            Thread.sleep(500);
            
            String currentUrl = driver.getCurrentUrl();
            if (!currentUrl.contains("inventory.html")) {
                logger.error("After clicking, navigated away from inventory page! Current URL: {}", currentUrl);
                throw new RuntimeException("Navigated away from inventory page after clicking. URL: " + currentUrl);
            }
            
            List<WebElement> updatedButtons = driver.findElements(By.cssSelector(".inventory_item button"));
            if (itemIndex < updatedButtons.size()) {
                String buttonTextAfter = updatedButtons.get(itemIndex).getText();
                logger.info("Button text after click: '{}'", buttonTextAfter);
                
                if ("Remove".equalsIgnoreCase(buttonTextAfter)) {
                    logger.info("✓ Product successfully added to cart (button changed to 'Remove')");
                } else {
                    logger.warn("Warning: Button text is '{}' instead of expected 'Remove'", buttonTextAfter);
                }
            }
        } catch (StaleElementReferenceException e) {
            logger.error("Stale element at index {} - retrying...", itemIndex);
            try {
                Thread.sleep(500);
                
                String currentUrl = driver.getCurrentUrl();
                logger.info("After stale element, current URL: {}", currentUrl);
                
                if (!currentUrl.contains("inventory.html")) {
                    throw new RuntimeException("Not on inventory page after stale element. URL: " + currentUrl);
                }
                
                List<WebElement> retryButtons = driver.findElements(By.cssSelector(".inventory_item button"));
                if (itemIndex < retryButtons.size()) {
                    String currentText = retryButtons.get(itemIndex).getText();
                    if ("Remove".equalsIgnoreCase(currentText)) {
                        logger.info("✓ Product was successfully added despite stale element");
                        return;
                    }
                }
            } catch (Exception retryEx) {
                logger.error("Retry failed: {}", retryEx.getMessage());
            }
            throw new RuntimeException("Failed to add item to cart at index " + itemIndex + " due to stale element", e);
        } catch (Exception e) {
            logger.error("Failed to add item at index {} to cart: {}", itemIndex, e.getMessage());
            logger.error("Current URL: {}", driver.getCurrentUrl());
            throw new RuntimeException("Failed to add item to cart at index " + itemIndex, e);
        }
    }

    public void addItemToCartByName(String itemName) {
        for (WebElement item : inventoryItems) {
            if (item.getText().contains(itemName)) {
                WebElement addButton = item.findElement(org.openqa.selenium.By.tagName("button"));
                BasePage.click(driver, addButton);
                break;
            }
        }
    }

    public void clickShoppingCart() {
        BasePage.click(driver, shoppingCartLink);
    }

    public int getCartItemCount() {
        try {
            List<WebElement> badges = driver.findElements(By.className("shopping_cart_badge"));
            if (!badges.isEmpty() && badges.get(0).isDisplayed()) {
                String badgeText = badges.get(0).getText();
                logger.info("Cart badge text: '{}'", badgeText);
                return Integer.parseInt(badgeText);
            }
            logger.info("Cart badge not displayed - cart is empty");
            return 0;
        } catch (Exception e) {
            logger.error("Error getting cart count: {}", e.getMessage());
            return 0;
        }
    }

    public void clickMenuButton() {
        BasePage.click(driver, menuButton);
    }

    public void clickLogout() {
        BasePage.click(driver, logoutLink);
    }
}
