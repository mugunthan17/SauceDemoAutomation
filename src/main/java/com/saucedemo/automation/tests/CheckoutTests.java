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

public class CheckoutTests {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutTests.class);
    private WebDriver driver;
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;

    @BeforeClass
    public void setUp() {
        BrowserUtil.initializeBrowser();
        driver = BrowserUtil.getDriver();
    }

    @AfterClass
    public void tearDown() {
        BrowserUtil.closeBrowser();
    }

    @BeforeMethod
    public void loginAndSetupCart(Object[] testData) {
        if (testData != null && testData.length > 0 && testData[0] instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> data = (Map<String, String>) testData[0];
            
            String username = data.getOrDefault("Username", "");
            String password = data.getOrDefault("Password", "");
            String testCase = data.getOrDefault("TestCase", "");
            String productIndexStr = data.getOrDefault("ProductIndex", "0");
            
            logger.info("========== Setup for Test Case: {} ==========", testCase);
            
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
            
            String[] indexStrings = productIndexStr.split(",");
            for (String indexStr : indexStrings) {
                int index = Integer.parseInt(indexStr.trim());
                inventoryPage.addItemToCart(index);
                logger.info("Step 2: Added product at index {} to cart", index);
            }
            
            inventoryPage.clickShoppingCart();
            logger.info("Step 3: Navigated to cart page");
            
            cartPage = new CartPage(driver);
            if (!cartPage.isCartPageDisplayed()) {
                logger.error("✗ Cart page NOT displayed - Skipping test");
                throw new SkipException("Cart page not displayed. Skipping test case: " + testCase);
            }
            
            cartPage.clickCheckout();
            logger.info("Step 4: Clicked checkout button");
            
            checkoutPage = new CheckoutPage(driver);
            logger.info("✓ Setup completed - Ready for checkout test");
        }
    }

    @AfterMethod
    public void logoutAfterTest(ITestResult result) {
        try {
            if (inventoryPage != null && result.getStatus() == ITestResult.SUCCESS) {
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

    @DataProvider(name = "checkoutDataProvider")
    public Iterator<Object[]> checkoutDataProvider(ITestContext context) {
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

    @Test(dataProvider = "checkoutDataProvider", description = "Data-driven checkout test from Excel")
    public void testCheckoutFunctionality(Map<String, String> testData) {
        String testCase = testData.getOrDefault("TestCase", "");
        String firstName = testData.getOrDefault("FirstName", "");
        String lastName = testData.getOrDefault("LastName", "");
        String postalCode = testData.getOrDefault("PostalCode", "");
        String expectedResult = testData.getOrDefault("ExpectedResult", "");
        String errorMessageContains = testData.getOrDefault("ErrorMessageContains", "");
        String description = testData.getOrDefault("Description", "");
        
        logger.info("========== Executing Test Case: {} ==========", testCase);
        logger.info("Description: {}", description);
        logger.info("Expected Result: {}", expectedResult);
        
        logger.info("Step 5: Entering checkout information");
        logger.info("  - First Name: {}", firstName.isEmpty() ? "<empty>" : firstName);
        logger.info("  - Last Name: {}", lastName.isEmpty() ? "<empty>" : lastName);
        logger.info("  - Postal Code: {}", postalCode.isEmpty() ? "<empty>" : postalCode);
        
        checkoutPage.enterFirstName(firstName);
        checkoutPage.enterLastName(lastName);
        checkoutPage.enterPostalCode(postalCode);
        checkoutPage.clickContinue();
        
        logger.info("Step 6: Clicked continue button");
        
        if ("SUCCESS".equalsIgnoreCase(expectedResult)) {
            logger.info("Step 7: Clicking finish to complete order");
            checkoutPage.clickFinish();
            
            boolean isOrderComplete = checkoutPage.isOrderComplete();
            logger.info("Step 8: Verifying order completion");
            
            if (isOrderComplete) {
                logger.info("✓ Order completion verification PASSED");
            } else {
                logger.error("✗ Order completion verification FAILED");
            }
            
            Assert.assertTrue(isOrderComplete, 
                "Order should be completed successfully");
                
        } else if ("ERROR".equalsIgnoreCase(expectedResult)) {
            logger.info("Step 7: Verifying error message is displayed");
            
            boolean errorDisplayed = checkoutPage.isErrorMessageDisplayed();
            
            if (errorDisplayed) {
                logger.info("✓ Error message is displayed as expected");
            } else {
                logger.error("✗ Error message is NOT displayed");
            }
            
            Assert.assertTrue(errorDisplayed, 
                "Error message should be displayed for invalid checkout information");
            
            if (errorMessageContains != null && !errorMessageContains.isEmpty()) {
                String actualError = checkoutPage.getErrorMessage();
                logger.info("Step 8: Verifying error message content");
                logger.info("Expected error to contain: {}", errorMessageContains);
                logger.info("Actual error message: {}", actualError);
                
                if (actualError.contains(errorMessageContains)) {
                    logger.info("✓ Error message verification PASSED");
                } else {
                    logger.error("✗ Error message verification FAILED");
                }
                
                Assert.assertTrue(actualError.contains(errorMessageContains), 
                    "Error message should contain: '" + errorMessageContains + "' but was: '" + actualError + "'");
            }
        }
        
        logger.info("========== Test Case {} Completed ==========", testCase);
    }
}
