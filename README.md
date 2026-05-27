# SauceDemo Automation Framework

**Enterprise-Grade Test Automation Framework for SauceDemo E-Commerce Application**

---

## 📖 Overview

This is a production-ready Selenium WebDriver automation framework designed for comprehensive testing of the [SauceDemo](https://www.saucedemo.com/) e-commerce web application. Built with Java 17, TestNG 7.3.0, and implementing a clean 4-layer architecture, this framework demonstrates industry best practices in test automation design, data-driven testing, and maintainable code structure.

**Technical Profile:**
- **Architecture:** 4-Layer Clean Architecture (Utils → API/Pages → Tests → TestData)
- **Design Pattern:** Page Object Model with Static Utility Methods
- **Data Strategy:** Excel-based Data-Driven Testing with TestNG DataProvider
- **Logging:** SLF4J/Logback with Test-Level File Separation
- **Build System:** Maven 3.x
- **Browser Management:** Centralized BrowserUtil (composition over inheritance)

**Application Under Test:** https://www.saucedemo.com/

---

## 🎯 What This Framework Does

This framework provides automated test coverage for all critical user journeys in the SauceDemo application:

### Functional Coverage

**Login & Authentication**
- Valid/invalid credential validation
- Locked user scenarios
- Empty field handling
- Session management

**Product Catalog & Shopping Cart**
- Product browsing and selection
- Add/remove items to cart
- Cart state management
- Multiple item handling with dynamic element finding

**Checkout & Order Completion**
- Customer information validation
- Order review and confirmation
- End-to-end purchase flow
- Form validation and error handling

### Technical Capabilities

✅ **Excel-Driven Test Execution** - Each row in Excel = separate test execution  
✅ **Comprehensive Logging** - Separate log file generated per test execution  
✅ **HTML Test Reports** - Test data displayed in formatted HTML tables  
✅ **Stale Element Handling** - Robust dynamic element re-finding  
✅ **Index Management** - Descending order removal to prevent shifting  
✅ **Browser Lifecycle Management** - Centralized BrowserUtil with proper cleanup  
✅ **Test Isolation** - @BeforeMethod/@AfterMethod for login/logout separation  

---

## 🏗️ Architecture

This framework implements a **4-layer clean architecture** that promotes separation of concerns, maintainability, and testability.

### Layer Structure

```
┌─────────────────────────────────────────────────────────────┐
│                     LAYER 4: TestData                       │
│  Excel files (.xlsx) containing test input and expected     │
│  results. Read by TestDataProvider and ExcelReader.         │
│  Location: src/main/resources/testdata/                     │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                      LAYER 3: Tests                         │
│  TestNG test classes with @Test methods. Tests are pure     │
│  orchestration - no direct WebDriver or element logic.      │
│  Uses @DataProvider for data-driven execution.              │
│  Location: src/main/java/com/saucedemo/tests/              │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    LAYER 2: API/Pages                       │
│  Page Object classes with static methods. Encapsulates      │
│  page-specific element interactions and business logic.     │
│  Uses composition, not inheritance.                         │
│  Location: src/main/java/com/saucedemo/pages/              │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                     LAYER 1: Utils                          │
│  Reusable utility classes: BrowserUtil, WaitUtil,           │
│  ExcelReader, TestDataProvider, Listeners.                  │
│  Provides foundational services to all upper layers.        │
│  Location: src/main/java/com/saucedemo/utils/              │
└─────────────────────────────────────────────────────────────┘
```

### Design Principles Applied

**Composition Over Inheritance**
- No BaseTest inheritance - tests use BrowserUtil directly
- Page classes use static utility methods
- Eliminates tight coupling and inheritance chains

**Single Responsibility**
- BrowserUtil: Browser lifecycle only
- WaitUtil: Wait operations only
- ExcelReader: Excel file reading only
- Each page class: One page's elements and actions only

**Dependency Inversion**
- Upper layers depend on abstractions (interfaces/utilities)
- Lower layers provide implementations
- WebDriver instance passed explicitly, not inherited

---

## 🗂️ Project Structure

```
saucedemo-automation/
│
├── pom.xml                                    # Maven build configuration
├── testng.xml                                 # TestNG suite with listeners
├── README.md                                  # This comprehensive guide
│
├── src/
│   ├── main/java/com/saucedemo/
│   │   │
│   │   ├── utils/                             # LAYER 1: Foundation utilities
│   │   │   ├── BrowserUtil.java               # Browser lifecycle management
│   │   │   ├── WaitUtil.java                  # Explicit wait operations
│   │   │   ├── ExcelReader.java               # Excel file reading
│   │   │   ├── TestDataProvider.java          # DataProvider for TestNG
│   │   │   ├── TestDataReportListener.java    # HTML table formatting in reports
│   │   │   └── TestLogger.java                # Test-level log file generation
│   │   │
│   │   ├── pages/                             # LAYER 2: Page Object classes
│   │   │   ├── BasePage.java                  # Common page utilities (static)
│   │   │   ├── LoginPage.java                 # Login page elements & actions
│   │   │   ├── InventoryPage.java             # Product catalog interactions
│   │   │   ├── CartPage.java                  # Shopping cart management
│   │   │   └── CheckoutPage.java              # Checkout flow handling
│   │   │
│   │   └── tests/                             # LAYER 3: Test execution
│   │       ├── LoginTests.java                # Login test scenarios
│   │       ├── CartTests.java                 # Cart functionality tests
│   │       ├── CheckoutTests.java             # Checkout process tests
│   │       └── EndToEndTests.java             # Complete user journey tests
│   │
│   ├── main/resources/
│   │   ├── config/
│   │   │   └── config.properties              # Configuration parameters
│   │   ├── drivers/                           # Optional local WebDriver binaries
│   │   ├── testdata/                          # LAYER 4: Excel test data files
│   │   │   ├── LoginData.xlsx
│   │   │   ├── CartData.xlsx
│   │   │   ├── CheckoutData.xlsx
│   │   │   └── EndToEndData.xlsx
│   │   └── TEMPLATES/                         # HTML templates for reporting
│   │
│   ├── logs/                                  # Test execution logs (generated)
│   │   └── *.log                              # Separate log file per test
│   │
│   └── test/
│       ├── java/                              # Empty (common-test pattern)
│       └── resources/                         # Empty
│
└── target/                                    # Maven build output
    ├── classes/                               # Compiled code
    ├── test-classes/                          # Compiled tests
    └── test-output/                           # TestNG reports
        ├── emailable-report.html              # Primary HTML report
        ├── index.html                         # Detailed TestNG report
        └── testng-results.xml                 # XML results for CI/CD
```

---

## 🔧 Technology Stack & Dependencies

| Component | Version | Purpose | Maven Coordinates |
|-----------|---------|---------|-------------------|
| **Java** | 17 | Programming language | - |
| **Maven** | 3.x | Build & dependency management | - |
| **Selenium WebDriver** | 4.21.0 | Browser automation | `org.seleniumhq.selenium:selenium-java` |
| **TestNG** | 7.3.0 | Test framework & assertions | `org.testng:testng` |
| **Apache POI** | 5.2.5 | Excel file operations | `org.apache.poi:poi-ooxml` |
| **WebDriverManager** | 5.8.0 | Automatic driver downloads | `io.github.bonigarcia:webdrivermanager` |
| **SLF4J API** | 2.0.7 | Logging facade | `org.slf4j:slf4j-api` |
| **Logback Classic** | 1.4.14 | Logging implementation | `ch.qos.logback:logback-classic` |

### Why These Versions?

- **TestNG 7.3.0:** Aligns with enterprise common-test project, avoids Eclipse plugin compatibility issues
- **Selenium 4.21.0:** Latest features with findElements() for dynamic element handling
- **Java 17:** LTS version with modern language features
- **Apache POI 5.2.5:** Full .xlsx support for data-driven testing

---

## 🚀 Getting Started

### Prerequisites

Ensure the following are installed and configured:

1. **Java Development Kit (JDK) 17**
   - Download: https://www.oracle.com/java/technologies/downloads/
   - Verify: `java -version` should show Java 17

2. **Apache Maven 3.x**
   - Download: https://maven.apache.org/download.cgi
   - Verify: `mvn -version` should show Maven 3.x

3. **Eclipse IDE** (or IntelliJ IDEA)
   - Download: https://www.eclipse.org/downloads/
   - Install TestNG plugin: Help → Eclipse Marketplace → Search "TestNG"

4. **Web Browser**
   - Chrome, Firefox, or Edge (latest version)
   - WebDriverManager will auto-download drivers

### Installation & Setup

```bash
# Clone or download the project
cd C:\Users\mugn\Documents\NM\saucedemo\saucedemo-automation

# Update Maven dependencies
mvn clean install

# Verify build success
mvn clean compile
```

### Import into Eclipse

1. File → Import → Maven → Existing Maven Projects
2. Browse to project folder
3. Click Finish
4. Right-click project → Maven → Update Project (Force Update)

---

## ▶️ Running Tests

### Option 1: Eclipse TestNG Runner (Recommended)

Right-click `testng.xml` → Run As → TestNG Suite

**What Happens:**
- TestNG reads suite configuration
- Listeners (TestLogger, TestDataReportListener) initialize
- Each test retrieves Excel data via DataProvider
- Each test creates separate log file (if loggerFileName parameter set)
- HTML report generated with test data tables

### Option 2: Maven Command Line

```bash
# Run all tests
mvn clean test

# Run with specific browser
mvn clean test -Dbrowser=chrome
mvn clean test -Dbrowser=firefox

# Run with suite file
mvn clean test -DsuiteXmlFile=testng.xml
```

### Option 3: Run Individual Test Class

Right-click test class (e.g., `LoginTests.java`) → Run As → TestNG Test

---

## 📊 Reports & Outputs

### 1. TestNG Emailable Report (Primary)

**Location:** `test-output/emailable-report.html`

**Contains:**
- ✅ Pass/Fail/Skip summary
- ⏱️ Execution time per test
- 📋 Test data used (formatted HTML table)
- 📝 Assertions and error messages
- 🎨 Color-coded results (green/red)

**View Report:**
```cmd
start test-output\emailable-report.html
```

### 2. Test Execution Logs

**Location:** `src/logs/`

**Log File Naming:**
- Suite-level: `SauceDemoLogs.log` (default)
- Test-level: `<TestName>_Logs.log` (e.g., `TC1_ValidLogin_Logs.log`)

**What's Logged:**
- Browser initialization/closure
- Page navigation
- Element interactions
- Test data values
- Assertions results
- Exceptions and errors

**Log Configuration:**
- Framework: SLF4J + Logback
- Level: INFO (configurable in logback.xml)
- Format: `[timestamp] [level] [class] - message`

**How Test-Level Logs Work:**
```xml
<!-- In testng.xml -->
<test name="Login Tests">
    <parameter name="loggerFileName" value="LoginTests_Logs.log"/>
</test>
```
TestLogger checks for `loggerFileName` parameter → creates separate PrintWriter → logs written to test-specific file.

### 3. TestNG XML Results

**Location:** `test-output/testng-results.xml`

Machine-readable format for CI/CD pipeline integration.

---

## 📝 Excel-Based Data-Driven Testing

### Concept

Instead of hardcoding test data in Java code, this framework externalizes test inputs and expected outcomes into Excel files. Each row in the Excel sheet becomes a separate test execution, enabling:
- **Non-technical test data management:** QA can update test cases without code changes
- **Scalability:** Add 100 test cases by adding 100 Excel rows
- **Traceability:** Test data visible in HTML reports

### Excel File Structure

**Location:** `src/main/resources/testdata/`

**Example: LoginData.xlsx**

| TestCase | Username | Password | ExpectedResult | ErrorMessage | Description |
|----------|----------|----------|----------------|--------------|-------------|
| TC1_ValidLogin | standard_user | secret_sauce | success | | Valid credentials |
| TC2_InvalidUser | invalid_user | secret_sauce | failure | Epic sadface: Username and password do not match | Invalid username |
| TC3_LockedUser | locked_out_user | secret_sauce | failure | Epic sadface: Sorry, this user has been locked out | Locked account |

**Column Mapping:**
- TestCase: Test identifier (used in reports)
- Username, Password: Test inputs
- ExpectedResult: success/failure (validation)
- ErrorMessage: Expected error text (for failures)
- Description: Test case description

### How It Works

**1. testng.xml Configuration**
```xml
<test name="Login Tests">
    <parameter name="testData" value="./src/main/resources/testdata/LoginData.xlsx:LoginTests"/>
</test>
```
Format: `<filePath>:<sheetName>`

**2. Test Class DataProvider**
```java
@DataProvider(name = "loginData")
public Object[][] getLoginData(ITestContext context) {
    String testDataFile = context.getCurrentXmlTest().getParameter("testData");
    return TestDataProvider.getFilteredTestData(testDataFile);
}
```

**3. Test Method Receives Data**
```java
@Test(dataProvider = "loginData")
public void testLogin(Map<String, String> testData) {
    String username = testData.get("Username");
    String password = testData.get("Password");
    // Test logic here
}
```

**4. Execution Flow**
- TestNG calls DataProvider for each test
- ExcelReader.readExcel() reads file and sheet
- Each row converted to Map<String, String>
- Test method executed once per row
- Test data logged and displayed in HTML report

---

## 🧪 Test Execution Architecture

### DataProvider Pattern

Each Excel row results in a **separate test execution** in TestNG reports:

```
LoginTests.testLogin[0] - TC1_ValidLogin - PASSED
LoginTests.testLogin[1] - TC2_InvalidUser - PASSED
LoginTests.testLogin[2] - TC3_LockedUser - PASSED
```

**Benefits:**
- Individual pass/fail visibility
- Parallel execution capability
- Isolated test data per execution

### Test Lifecycle with @BeforeMethod/@AfterMethod

Tests follow a clean setup/teardown pattern:

```java
@BeforeMethod
public void loginBeforeTest(Object[] testData) {
    driver = BrowserUtil.initializeBrowser(ConfigReader.getBrowser());
    LoginPage.doLogin(driver, username, password);
    if (!success) throw new SkipException("Login failed");
    CartPage.clearCart(driver);  // Start with clean state
}

@Test(dataProvider = "data")
public void testCartFunctionality(Map<String, String> testData) {
    // Pure test logic, no login/logout
}

@AfterMethod
public void logoutAfterTest() {
    InventoryPage.clickLogout(driver);
    BrowserUtil.closeBrowser(driver);
}
```

**Why This Pattern?**
- **Separation of concerns:** Login/logout separated from test logic
- **Test isolation:** Each test starts with fresh logged-in state
- **Clean cart state:** clearCart() prevents data accumulation
- **Automatic cleanup:** Browser always closed, even on test failure

---

## 🛠️ Key Technical Implementations

### 1. Stale Element Handling

**Problem:** After clicking "Add to cart", element becomes stale because DOM updates.

**Solution:** Re-find element after interaction
```java
public static void addItemToCart(WebDriver driver, int productIndex) {
    WebElement addButton = driver.findElements(addToCartButtons).get(productIndex);
    addButton.click();
    
    // Re-find element after DOM change
    WebElement updatedButton = driver.findElements(addToCartButtons).get(productIndex);
    String buttonText = updatedButton.getText();
    
    if (!"Remove".equals(buttonText)) {
        logger.error("Button text did not change to 'Remove'");
    }
}
```

### 2. Index Shifting Prevention

**Problem:** Removing items by index 2, 4, 6 → after removing 2, original 4 becomes 3.

**Solution:** Sort indices and remove in descending order (highest first)
```java
int[] removalIndices = {2, 4, 6};
Arrays.sort(removalIndices);  // [2, 4, 6]

// Remove in reverse order
for (int i = removalIndices.length - 1; i >= 0; i--) {
    CartPage.removeItemFromCart(driver, removalIndices[i]);
}
```

### 3. JavaScript Click Fallback

**Problem:** Finish button sometimes not clickable due to overlays.

**Solution:** Try normal click, fallback to JavaScript
```java
public static void clickFinishWithRetry(WebDriver driver) {
    try {
        waitForElementClickable(driver, finishButton, 10).click();
    } catch (Exception e) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", driver.findElement(finishButton));
    }
}
```

### 4. Test-Level Log File Separation

**Problem:** All tests log to one file → hard to debug specific test.

**Solution:** ConcurrentHashMap with test-specific PrintWriters
```java
@Override
public void onStart(ITestContext context) {
    String loggerFileName = context.getCurrentXmlTest().getParameter("loggerFileName");
    
    if (loggerFileName != null) {
        PrintWriter testWriter = new PrintWriter(new FileWriter("src/logs/" + loggerFileName));
        testWriters.put(context.getName(), testWriter);
    }
}
```

### 5. Dynamic Element Finding

**Problem:** PageFactory @FindBy elements become stale after DOM updates.

**Solution:** Use driver.findElements() in real-time
```java
// Instead of:
@FindBy(css = ".btn_inventory")
List<WebElement> addToCartButtons;

// Use:
private static By addToCartButtons = By.cssSelector(".btn_inventory");

public static void addItem(WebDriver driver, int index) {
    List<WebElement> buttons = driver.findElements(addToCartButtons);  // Fresh elements
    buttons.get(index).click();
}
```

---

## ⚙️ Configuration

### config.properties

**Location:** `src/main/resources/config/config.properties`

```properties
browser=chrome
baseUrl=https://www.saucedemo.com/
implicitWait=10
explicitWait=10
```

**Usage in Code:**
```java
String browser = ConfigReader.getBrowser();
WebDriver driver = BrowserUtil.initializeBrowser(browser);
```

### testng.xml

**Location:** Project root

```xml
<suite name="SauceDemo Suite" parallel="tests" thread-count="2">
    
    <listeners>
        <listener class-name="com.saucedemo.utils.TestLogger"/>
        <listener class-name="com.saucedemo.utils.TestDataReportListener"/>
    </listeners>
    
    <test name="Login Tests">
        <parameter name="testData" value="./src/main/resources/testdata/LoginData.xlsx:LoginTests"/>
        <parameter name="loggerFileName" value="LoginTests_Logs.log"/>
        <classes>
            <class name="com.saucedemo.tests.LoginTests"/>
        </classes>
    </test>
    
    <test name="Cart Tests">
        <parameter name="testData" value="./src/main/resources/testdata/CartData.xlsx:CartTests"/>
        <parameter name="loggerFileName" value="CartTests_Logs.log"/>
        <classes>
            <class name="com.saucedemo.tests.CartTests"/>
        </classes>
    </test>
</suite>
```

**Key Parameters:**
- `testData`: Excel file path and sheet name
- `loggerFileName`: Separate log file for each test

---

## 📚 Test Scenarios

### LoginTests.java

| Test Case | Scenario | Expected Result |
|-----------|----------|-----------------|
| TC1 | Valid credentials | Login success, navigate to inventory |
| TC2 | Invalid username | Login fails, error message displayed |
| TC3 | Invalid password | Login fails, error message displayed |
| TC4 | Empty credentials | Login fails, error message displayed |
| TC5 | Locked out user | Login fails, "locked out" message |

### CartTests.java

| Test Case | Action | Expected Result |
|-----------|--------|-----------------|
| TC1 | ADD | Products added, cart count updated |
| TC2 | ADD_REMOVE | Products added then removed, cart empty |
| TC3 | ADD_REMOVE_ONE | Add 3, remove 1, cart count = 2 |

### CheckoutTests.java

| Test Case | Scenario | Expected Result |
|-----------|----------|-----------------|
| TC1 | Valid checkout | Order completed successfully |
| TC2 | Missing first name | Error displayed |
| TC3 | Missing postal code | Error displayed |

### EndToEndTests.java

| Test Case | Flow | Expected Result |
|-----------|------|-----------------|
| TC1-TC4 | Login → Add products → Checkout → Complete | Full purchase flow successful |

---

## 🐛 Troubleshooting

### Issue: Tests fail with "stale element reference"
**Cause:** DOM updated after element found  
**Solution:** Already handled in InventoryPage.addItemToCart() with re-finding

### Issue: Wrong cart count after removal
**Cause:** Index shifting when removing multiple items  
**Solution:** Already handled with descending order removal in EndToEndTests

### Issue: Only one log file created
**Cause:** Missing `loggerFileName` parameter in testng.xml  
**Solution:** Add `<parameter name="loggerFileName" value="TestName_Logs.log"/>` to each `<test>`

### Issue: "WebDriver executable not found"
**Cause:** Driver not downloaded  
**Solution:** WebDriverManager auto-downloads, or place driver in `src/main/resources/drivers/`

### Issue: Excel file not found
**Cause:** Incorrect path in testng.xml  
**Solution:** Use relative path: `./src/main/resources/testdata/LoginData.xlsx:LoginTests`

### Issue: Maven build fails
**Cause:** Dependency download issues  
**Solution:** `mvn clean install -U` (force update)

---

## 🎯 Best Practices Demonstrated

✅ **4-Layer Architecture** - Clean separation of concerns  
✅ **Composition over Inheritance** - No BaseTest, use BrowserUtil  
✅ **Static Utility Methods** - Easier to call, no instance needed  
✅ **DataProvider Pattern** - Each Excel row = separate test  
✅ **Test Isolation** - @BeforeMethod/@AfterMethod for login/logout  
✅ **Dynamic Element Finding** - Avoid stale elements  
✅ **Descending Order Removal** - Prevent index shifting  
✅ **JavaScript Click Fallback** - Handle overlay issues  
✅ **Comprehensive Logging** - Separate log file per test  
✅ **Excel-Based Data** - QA-friendly test data management  
✅ **No Comments in Code** - Self-documenting code with clear naming  

---

## 📞 Support & Reference

This framework aligns with the enterprise-grade **common-test** project structure:

**Reference:** `C:\Users\mugn\git\common-test-R26.6.0-new\common-test\`

**Alignment:**
- ✅ TestNG 7.3.0 (same version)
- ✅ Tests in `src/main/java` (same structure)
- ✅ Excel-based data-driven testing (same approach)
- ✅ No test scope on TestNG dependency (same configuration)

---

## 🎓 Learning Resources

- [Selenium WebDriver Documentation](https://www.selenium.dev/documentation/)
- [TestNG Official Guide](https://testng.org/doc/documentation-main.html)
- [Apache POI Excel Tutorial](https://poi.apache.org/components/spreadsheet/)
- [Maven Complete Reference](https://maven.apache.org/guides/)
- [Page Object Model Pattern](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/)

---
