package com.saucedemo.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CartPage {

    private WebDriver driver;
    private static final Logger logger = LoggerFactory.getLogger(CartPage.class);

    @FindBy(className = "title")
    private WebElement pageTitle;

    @FindBy(className = "cart_item")
    private List<WebElement> cartItems;

    @FindBy(id = "continue-shopping")
    private WebElement continueShoppingButton;

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    @FindBy(className = "cart_button")
    private List<WebElement> removeButtons;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isCartPageDisplayed() {
        try {
            BasePage.waitForVisible(driver, pageTitle);
            return pageTitle.getText().equals("Your Cart");
        } catch (Exception e) {
            return false;
        }
    }

    public int getCartItemsCount() {
        return cartItems.size();
    }

    public void removeItem(int itemIndex) {
        if (itemIndex < removeButtons.size()) {
            BasePage.click(driver, removeButtons.get(itemIndex));
        }
    }

    public void clickContinueShopping() {
        BasePage.click(driver, continueShoppingButton);
    }

    public void clickCheckout() {
        BasePage.click(driver, checkoutButton);
    }
    
    public int[] parseProductIndices(String productIndexStr) {
        if (productIndexStr == null || productIndexStr.trim().isEmpty()) {
            return new int[0];
        }
        
        String[] indexStrings = productIndexStr.split(",");
        int[] indices = new int[indexStrings.length];
        
        for (int i = 0; i < indexStrings.length; i++) {
            indices[i] = Integer.parseInt(indexStrings[i].trim());
        }
        
        return indices;
    }
    
    public boolean clearCart(InventoryPage inventoryPage) {
        try {
            int currentCartCount = inventoryPage.getCartItemCount();
            
            if (currentCartCount == 0) {
                logger.info("Cart is already empty - no clearing needed");
                return true;
            }
            
            logger.info("Clearing existing {} item(s) from cart", currentCartCount);
            inventoryPage.clickShoppingCart();
            
            for (int i = 0; i < currentCartCount; i++) {
                removeItem(0);
            }
            
            int remainingItems = getCartItemsCount();
            if (remainingItems == 0) {
                logger.info("Cart cleared successfully - all {} item(s) removed", currentCartCount);
                driver.navigate().back();
                return true;
            } else {
                logger.error("Failed to clear cart - {} item(s) still remaining", remainingItems);
                driver.navigate().back();
                return false;
            }
        } catch (Exception e) {
            logger.error("Exception occurred while clearing cart: {}", e.getMessage());
            return false;
        }
    }
    
}
