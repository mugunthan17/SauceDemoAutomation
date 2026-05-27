package com.saucedemo.automation.tests;

import com.saucedemo.automation.pages.LoginPage;
import com.saucedemo.automation.pages.InventoryPage;
import com.saucedemo.automation.pages.CartPage;
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

public class CartTests {

    private static final Logger logger = LoggerFactory.getLogger(CartTests.class);
    private WebDriver driver;
    private CartPage cartPage;
    private InventoryPage inventoryPage;

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
                inventoryPage.clickMenuButton();
                Thread.sleep(500);
                inventoryPage.clickLogout();
                logger.info("✓ Logout successful");
            }
        } catch (Exception e) {
            logger.error("Error during logout: {}", e.getMessage());
        }
    }

    @DataProvider(name = "cartDataProvider")
    public Iterator<Object[]> cartDataProvider(ITestContext context) {
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

    @Test(dataProvider = "cartDataProvider", description = "Data-driven cart test from Excel")
    public void testCartFunctionality(Map<String, String> testData) {
        String testCase = testData.getOrDefault("TestCase", "");
        String productIndexStr = testData.getOrDefault("ProductIndex", "");
        String action = testData.getOrDefault("Action", "");
        String expectedCartCountStr = testData.getOrDefault("ExpectedCartCount", "0");
        String description = testData.getOrDefault("Description", "");
        
        int expectedCartCount = Integer.parseInt(expectedCartCountStr);
        
        logger.info("========== Executing Test Case: {} ==========", testCase);
        logger.info("Description: {}", description);
        logger.info("Action: {}", action);
        logger.info("Expected Cart Count: {}", expectedCartCount);
        
        int[] productIndices = cartPage.parseProductIndices(productIndexStr);
        logger.info("Step 1: Processing action '{}' for products at indices: {}", action, productIndexStr);
        
        if ("ADD".equalsIgnoreCase(action)) {
            for (int index : productIndices) {
                logger.info("  - Adding product at index {} to cart", index);
                inventoryPage.addItemToCart(index);
            }
            
            int actualCartCount = inventoryPage.getCartItemCount();
            logger.info("Step 2: Verifying cart count - Expected: {}, Actual: {}", expectedCartCount, actualCartCount);
            
            if (actualCartCount == expectedCartCount) {
                logger.info("✓ Cart count verification PASSED");
            } else {
                logger.error("✗ Cart count verification FAILED");
            }
            
            Assert.assertEquals(actualCartCount, expectedCartCount, 
                "Cart should contain " + expectedCartCount + " item(s)");
                
        } else if ("ADD_REMOVE".equalsIgnoreCase(action)) {
            for (int index : productIndices) {
                logger.info("  - Adding product at index {} to cart", index);
                inventoryPage.addItemToCart(index);
            }
            
            logger.info("Step 2: Navigating to cart page");
            inventoryPage.clickShoppingCart();
            
            Assert.assertTrue(cartPage.isCartPageDisplayed(), "Cart page should be displayed");
            
            int itemsToRemove = productIndices.length;
            logger.info("Step 3: Removing {} item(s) from cart", itemsToRemove);
            for (int i = 0; i < itemsToRemove; i++) {
                logger.info("  - Removing item at index 0");
                cartPage.removeItem(0);
            }
            
            int actualCartCount = cartPage.getCartItemsCount();
            logger.info("Step 4: Verifying cart count after removal - Expected: {}, Actual: {}",expectedCartCount, actualCartCount);
            
            if (actualCartCount == expectedCartCount) {
                logger.info("✓ Cart count verification PASSED");
            } else {
                logger.error("✗ Cart count verification FAILED");
            }
            
            Assert.assertEquals(actualCartCount, expectedCartCount, 
                "Cart should contain " + expectedCartCount + " item(s) after removal");
                
        } else if ("ADD_REMOVE_ONE".equalsIgnoreCase(action)) {
            for (int index : productIndices) {
                logger.info("  - Adding product at index {} to cart", index);
                inventoryPage.addItemToCart(index);
            }
            
            logger.info("Step 2: Navigating to cart page");
            inventoryPage.clickShoppingCart();
            
            Assert.assertTrue(cartPage.isCartPageDisplayed(), "Cart page should be displayed");
            
            logger.info("Step 3: Removing one item from cart");
            cartPage.removeItem(0);
            
            int actualCartCount = cartPage.getCartItemsCount();
            logger.info("Step 4: Verifying cart count after removing one item - Expected: {}, Actual: {}",expectedCartCount, actualCartCount);
            
            if (actualCartCount == expectedCartCount) {
                logger.info("✓ Cart count verification PASSED");
            } else {
                logger.error("✗ Cart count verification FAILED");
            }
            
            Assert.assertEquals(actualCartCount, expectedCartCount, 
                "Cart should contain " + expectedCartCount + " item(s) after removing one item");
        }
        
        logger.info("========== Test Case {} Completed ==========", testCase);
    }
        
}
