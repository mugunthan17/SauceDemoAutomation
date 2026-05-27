package com.saucedemo.automation.tests;

import com.saucedemo.automation.pages.LoginPage;
import com.saucedemo.automation.pages.InventoryPage;
import com.saucedemo.automation.utils.BrowserUtil;
import com.saucedemo.automation.utils.ExcelReader;
import com.saucedemo.automation.utils.TestDataProvider;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LoginTests {

    private static final Logger logger = LoggerFactory.getLogger(LoginTests.class);
    private WebDriver driver;

    @BeforeClass
    public void setUp() {
        BrowserUtil.initializeBrowser();
        driver = BrowserUtil.getDriver();
    }

    @AfterClass
    public void tearDown() {
        BrowserUtil.closeBrowser();
    }

    @DataProvider(name = "loginDataProvider")
    public Iterator<Object[]> loginDataProvider(ITestContext context) {
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

    @Test(dataProvider = "loginDataProvider", description = "Data-driven login test from Excel")
    public void testLogin(Map<String, String> testData) {
        String testCase = testData.getOrDefault("TestCase", "");
        String username = testData.getOrDefault("Username", "");
        String password = testData.getOrDefault("Password", "");
        String expectedResult = testData.getOrDefault("ExpectedResult", "");
        String errorMessage = testData.getOrDefault("ErrorMessage", "");
        String description = testData.getOrDefault("Description", "");
        
        username = (username != null) ? username : "";
        password = (password != null) ? password : "";
        
        logger.info("========== Executing Test Case: {} ==========", testCase);
        logger.info("Description: {}", description);
        logger.info("Expected Result: {}", expectedResult);
        
        BrowserUtil.navigateToBaseUrl();
        LoginPage loginPage = new LoginPage(driver);
        boolean loggedIn = loginPage.doLogin(username, password);   
        if(!loggedIn) {
        	String message = "Test Failed to Login to sauce demo webpage";
        	Assert.fail(message);
        }
        
        if ("SUCCESS".equalsIgnoreCase(expectedResult)) {
            logger.info("Step 4: Verifying successful login - Checking if inventory page is displayed");
            InventoryPage inventoryPage = new InventoryPage(driver);
            boolean isDisplayed = inventoryPage.isInventoryPageDisplayed();
            
            if (isDisplayed) {
                logger.info("✓ Verification PASSED: Inventory page is displayed as expected");
            } else {
                logger.error("✗ Verification FAILED: Inventory page is NOT displayed");
            }
            
            Assert.assertTrue(isDisplayed, 
                "Inventory page should be displayed after successful login");
        } else {
            logger.info("Step 4: Verifying login failure - Checking if error message is displayed");
            boolean errorDisplayed = loginPage.isErrorMessageDisplayed();
            
            if (errorDisplayed) {
                logger.info("✓ Error message is displayed as expected");
            } else {
                logger.error("✗ Error message is NOT displayed");
            }
            
            Assert.assertTrue(errorDisplayed, 
                "Error message should be displayed for invalid credentials");
            
            if (errorMessage != null && !errorMessage.isEmpty()) {
                String actualError = loginPage.getErrorMessage();
                logger.info("Step 5: Verifying error message content");
                logger.info("Expected error to contain: {}", errorMessage);
                logger.info("Actual error message: {}", actualError);
                
                if (actualError.contains(errorMessage)) {
                    logger.info("✓ Error message verification PASSED");
                } else {
                    logger.error("✗ Error message verification FAILED");
                }
                
                Assert.assertTrue(actualError.contains(errorMessage), 
                    "Error message should contain: '" + errorMessage + "' but was: '" + actualError + "'");
            }
        } 
        logger.info("========== Test Case {} Completed ==========", testCase);
    }
    
    
}
