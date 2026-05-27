package com.saucedemo.automation.tests;

import com.saucedemo.automation.pages.LoginPage;
import com.saucedemo.automation.pages.InventoryPage;
import com.saucedemo.automation.pages.CartPage;
import com.saucedemo.automation.pages.CheckoutPage;
import com.saucedemo.automation.utils.BrowserUtil;
import com.saucedemo.automation.utils.ExcelReader;
import com.saucedemo.automation.utils.TestDataProvider;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EndToEndTests {

    private static final Logger logger = LoggerFactory.getLogger(EndToEndTests.class);
    private WebDriver driver;
    private InventoryPage inventoryPage;
    private CartPage cartPage;

    @BeforeClass
    public void setUp() {
        BrowserUtil.initializeBrowser();
        driver = BrowserUtil.getDriver();
        cartPage = new CartPage(driver);
    }

    @AfterClass
    public void tearDown() {
        BrowserUtil.closeBrowser();
    }

    @BeforeMethod
    public void loginBeforeTest(Object[] testData) {
        if (testData != null && testData.length > 0 && testData[0] instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> data = (Map<String, String>) testData[0];
            
            String username = data.getOrDefault("Username", "");
            String password = data.getOrDefault("Password", "");
            String testCase = data.getOrDefault("TestCase", "");
            
            logger.info("========== Login for Test Case: {} ==========", testCase);
            
            BrowserUtil.navigateToBaseUrl();
            logger.info("Step 1: Navigated to base URL");
            
            LoginPage loginPage = new LoginPage(driver);
            boolean loggedIn = loginPage.doLogin(username, password);
            
            if (!loggedIn) {
                logger.error("✗ Login FAILED for test case: {} - Skipping test", testCase);
                throw new SkipException("Login failed for user: " + username + ". Skipping test case: " + testCase);
            }
            
            inventoryPage = new InventoryPage(driver);
            boolean isInventoryDisplayed = inventoryPage.isInventoryPageDisplayed();
            
            if (!isInventoryDisplayed) {
                logger.error("✗ Inventory page NOT displayed after login - Skipping test");
                throw new SkipException("Inventory page not displayed after login. Skipping test case: " + testCase);
            }
            
            logger.info("✓ Login SUCCESSFUL - Inventory page displayed");
            
            boolean cartCleared = cartPage.clearCart(inventoryPage);
            if (!cartCleared) {
                logger.error("✗ Failed to clear cart - Skipping test");
                throw new SkipException("Failed to clear cart. Skipping test case: " + testCase);
            }
            logger.info("✓ Cart cleared successfully");
        }
    }

    @AfterMethod
    public void logoutAfterTest(ITestResult result) {
        try {
            if (inventoryPage != null) {
                logger.info("Logging out from application");
                BrowserUtil.navigateToBaseUrl();
                Thread.sleep(300);
                inventoryPage = new InventoryPage(driver);
                inventoryPage.clickMenuButton();
                Thread.sleep(500);
                inventoryPage.clickLogout();
                logger.info("✓ Logout successful");
            }
        } catch (Exception e) {
            logger.error("Error during logout: {}", e.getMessage());
        }
    }

    @DataProvider(name = "endToEndDataProvider")
    public Iterator<Object[]> endToEndDataProvider(ITestContext context) {
        String testData = context.getCurrentXmlTest().getParameter("testData");
        String testDataSets = context.getCurrentXmlTest().getParameter("testDataSets");
        
        ExcelReader reader = ExcelReader.fromParameter(testData);
        List<Map<String, String>> testDataList = TestDataProvider.getFilteredTestData(reader, testDataSets);
        reader.close();
        
        List<Object[]> dataProviderList = new ArrayList<>();
        for (Map<String, String> data : testDataList) {
            dataProviderList.add(new Object[]{data});
        }
        
        return dataProviderList.iterator();
    }

    @Test(dataProvider = "endToEndDataProvider", description = "Data-driven end-to-end purchase flow test")
    public void testEndToEndPurchaseFlow(Map<String, String> testData) {
        String testCase = testData.getOrDefault("TestCase", "");
        String productIndexStr = testData.getOrDefault("ProductsToAdd", "");
        String removeIndexStr = testData.getOrDefault("ProductsToRemove", "");
        String firstName = testData.getOrDefault("FirstName", "");
        String lastName = testData.getOrDefault("LastName", "");
        String postalCode = testData.getOrDefault("PostalCode", "");
        String expectedResult = testData.getOrDefault("ExpectedResult", "");
        String description = testData.getOrDefault("Description", "");
        
        logger.info("========== Executing Test Case: {} ==========", testCase);
        logger.info("Description: {}", description);
        logger.info("Expected Result: {}", expectedResult);
        
        logger.info("Step 1: Waiting for inventory items to load");
        inventoryPage.waitForInventoryItemsToLoad();
        
        int[] productIndices = cartPage.parseProductIndices(productIndexStr);
        logger.info("Step 2: Adding {} product(s) to cart", productIndices.length);
        
        for (int index : productIndices) {
            logger.info("  - Adding product at index {} to cart", index);
            inventoryPage.addItemToCart(index);
        }
        
        int expectedCartCount = productIndices.length;
        int actualCartCount = inventoryPage.getCartItemCount();
        logger.info("Step 3: Verifying cart count - Expected: {}, Actual: {}", 
            expectedCartCount, actualCartCount);
        
        if (actualCartCount == expectedCartCount) {
            logger.info("✓ Cart count verification PASSED");
        } else {
            logger.error("✗ Cart count verification FAILED");
        }
        
        Assert.assertEquals(actualCartCount, expectedCartCount, 
            "Cart should contain " + expectedCartCount + " item(s)");
        
        logger.info("Step 4: Navigating to cart page");
        inventoryPage.clickShoppingCart();
        
        Assert.assertTrue(cartPage.isCartPageDisplayed(), "Cart page should be displayed");
        logger.info("✓ Cart page displayed");
        
        int cartItemsBeforeRemoval = cartPage.getCartItemsCount();
        Assert.assertEquals(cartItemsBeforeRemoval, expectedCartCount, 
            "Cart should show " + expectedCartCount + " items");
        
        if (removeIndexStr != null && !removeIndexStr.trim().isEmpty()) {
            int[] removeIndices = cartPage.parseProductIndices(removeIndexStr);
            logger.info("Step 5: Removing {} item(s) from cart", removeIndices.length);
            
            java.util.Arrays.sort(removeIndices);
            for (int i = removeIndices.length - 1; i >= 0; i--) {
                logger.info("  - Removing item at index {}", removeIndices[i]);
                cartPage.removeItem(removeIndices[i]);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    logger.error("Sleep interrupted: {}", e.getMessage());
                }
            }
            
            int itemsAfterRemoval = cartPage.getCartItemsCount();
            int expectedAfterRemoval = expectedCartCount - removeIndices.length;
            logger.info("Step 6: Verifying cart count after removal - Expected: {}, Actual: {}", 
                expectedAfterRemoval, itemsAfterRemoval);
            
            if (itemsAfterRemoval == expectedAfterRemoval) {
                logger.info("✓ Cart count after removal verification PASSED");
            } else {
                logger.error("✗ Cart count after removal verification FAILED");
            }
            
            Assert.assertEquals(itemsAfterRemoval, expectedAfterRemoval, 
                "Cart should contain " + expectedAfterRemoval + " items after removal");
        } else {
            logger.info("Step 5: No items to remove - proceeding to checkout");
        }
        
        logger.info("Step 6: Proceeding to checkout");
        cartPage.clickCheckout();
        
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        logger.info("Step 7: Entering checkout information");
        logger.info("  - First Name: {}", firstName);
        logger.info("  - Last Name: {}", lastName);
        logger.info("  - Postal Code: {}", postalCode);
        
        checkoutPage.enterFirstName(firstName);
        checkoutPage.enterLastName(lastName);
        checkoutPage.enterPostalCode(postalCode);
        checkoutPage.clickContinue();
        
        logger.info("Step 8: Clicked continue on checkout page");
        logger.info("Current URL after clicking continue: {}", checkoutPage.getCurrentUrl());
        logger.info("Current page title: {}", checkoutPage.getPageTitle());
        
        logger.info("Step 9: Verifying checkout overview page is displayed");
        boolean isOverviewPage = checkoutPage.isCheckoutOverviewPage();
        
        logger.info("Overview page check result: {}", isOverviewPage);
        
        if (!isOverviewPage) {
            logger.error("✗ Checkout overview page NOT displayed - Current URL: {}", driver.getCurrentUrl());
            logger.error("Checking for validation errors...");
            
            if (checkoutPage.isErrorMessageDisplayed()) {
                String errorMsg = checkoutPage.getErrorMessage();
                logger.error("✗ Error message found: {}", errorMsg);
                Assert.fail("Checkout failed with error: " + errorMsg);
            } else {
                logger.error("✗ No error message displayed - page may not have loaded correctly");
                logger.error("Taking screenshot for debugging...");
                Assert.fail("Checkout overview page not displayed and no error message found. URL: " + driver.getCurrentUrl());
            }
        }
        
        logger.info("✓ Checkout overview page displayed");
        
        logger.info("Step 10: Verifying finish button is displayed");
        boolean isFinishButtonDisplayed = checkoutPage.isFinishButtonDisplayed();
        logger.info("Finish button displayed: {}", isFinishButtonDisplayed);
        
        if (!isFinishButtonDisplayed) {
            logger.error("✗ Finish button is NOT displayed on overview page");
            logger.error("Current URL: {}", driver.getCurrentUrl());
            Assert.fail("Finish button not displayed on checkout overview page. URL: " + driver.getCurrentUrl());
        }
        
        logger.info("✓ Finish button is displayed");
        logger.info("Step 11: Completing the order");
        
        try {
            checkoutPage.clickFinishWithRetry();
            logger.info("✓ Successfully clicked finish button");
        } catch (Exception e) {
            logger.error("✗ Failed to click finish button: {}", e.getMessage());
            logger.error("Current URL: {}", driver.getCurrentUrl());
            Assert.fail("Failed to click finish button: " + e.getMessage());
        }
        
        boolean isOrderComplete = checkoutPage.isOrderComplete();
        logger.info("Step 12: Verifying order completion");
        
        if (isOrderComplete) {
            logger.info("✓ Order completion verification PASSED");
        } else {
            logger.error("✗ Order completion verification FAILED");
        }
        
        Assert.assertTrue(isOrderComplete, "Order should be completed successfully");
        
        logger.info("========== Test Case {} Completed ==========", testCase);
    }
}
