package com.saucedemo.automation.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TestLogger implements ITestListener, ISuiteListener {
    
    private static final Logger logger = LoggerFactory.getLogger(TestLogger.class);
    private static String suitLogFilePath;
    private static PrintWriter suiteLogWriter;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private Map<String, PrintWriter> testLogWriters = new ConcurrentHashMap<>();
    private Map<String, String> testLogPaths = new ConcurrentHashMap<>();
    
    @Override
    public void onStart(ISuite suite) {
        String logFileName = suite.getParameter("loggerFileName");
        if (logFileName == null || logFileName.trim().isEmpty()) {
            logFileName = "SauceDemoLogs.log";
        }
        
        try {
            Path logsDir = Paths.get("src/logs");
            if (!Files.exists(logsDir)) {
                Files.createDirectories(logsDir);
            }
            
            suitLogFilePath = "src/logs/" + logFileName;
            suiteLogWriter = new PrintWriter(new BufferedWriter(new FileWriter(suitLogFilePath, false)), true);
            
            logToSuite("================================================================================");
            logToSuite("TEST SUITE STARTED: " + suite.getName());
            logToSuite("Start Time: " + dateFormat.format(new Date()));
            logToSuite("================================================================================");
            logToSuite("");
            
        } catch (IOException e) {
            logger.error("Failed to create suite log file: " + suitLogFilePath, e);
        }
    }
    
    @Override
    public void onFinish(ISuite suite) {
        if (suiteLogWriter != null) {
            logToSuite("");
            logToSuite("================================================================================");
            logToSuite("TEST SUITE COMPLETED: " + suite.getName());
            logToSuite("End Time: " + dateFormat.format(new Date()));
            
            Map<String, ISuiteResult> results = suite.getResults();
            int totalTests = 0;
            int passed = 0;
            int failed = 0;
            int skipped = 0;
            
            for (ISuiteResult result : results.values()) {
                ITestContext context = result.getTestContext();
                totalTests += context.getAllTestMethods().length;
                passed += context.getPassedTests().size();
                failed += context.getFailedTests().size();
                skipped += context.getSkippedTests().size();
            }
            
            logToSuite("Total Tests: " + totalTests);
            logToSuite("Passed: " + passed);
            logToSuite("Failed: " + failed);
            logToSuite("Skipped: " + skipped);
            logToSuite("================================================================================");
            
            suiteLogWriter.flush();
            suiteLogWriter.close();
            logger.info("Suite logs written to: " + suitLogFilePath);
        }
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        String testContextName = result.getTestContext().getName();
        String testName = getTestName(result);
        
        logToWriter(testContextName, ">>> TEST STARTED: " + testName);
        logToWriter(testContextName, "    Class: " + result.getTestClass().getName());
        logToWriter(testContextName, "    Method: " + result.getMethod().getMethodName());
        logToWriter(testContextName, "    Description: " + result.getMethod().getDescription());
        
        Object[] parameters = result.getParameters();
        if (parameters != null && parameters.length > 0) {
            logToWriter(testContextName, "    Test Data:");
            for (int i = 0; i < parameters.length; i++) {
                Object param = parameters[i];
                if (param instanceof Map) {
                    Map<String, String> dataMap = (Map<String, String>) param;
                    for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                        logToWriter(testContextName, "        " + entry.getKey() + " = " + entry.getValue());
                    }
                } else {
                    logToWriter(testContextName, "        Parameter[" + i + "] = " + param);
                }
            }
        }
        logToWriter(testContextName, "");
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        String testContextName = result.getTestContext().getName();
        String testName = getTestName(result);
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logToWriter(testContextName, "<<< TEST PASSED: " + testName);
        logToWriter(testContextName, "    Duration: " + duration + " ms");
        logToWriter(testContextName, "    End Time: " + dateFormat.format(new Date(result.getEndMillis())));
        logToWriter(testContextName, "");
        logToWriter(testContextName, "-----------------------------------------------------------");
        logToWriter(testContextName, "");
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testContextName = result.getTestContext().getName();
        String testName = getTestName(result);
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logToWriter(testContextName, "<<< TEST FAILED: " + testName);
        logToWriter(testContextName, "    Duration: " + duration + " ms");
        logToWriter(testContextName, "    End Time: " + dateFormat.format(new Date(result.getEndMillis())));
        
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            logToWriter(testContextName, "    Failure Reason: " + throwable.getMessage());
            logToWriter(testContextName, "    Exception Type: " + throwable.getClass().getName());
            
            logToWriter(testContextName, "    Stack Trace:");
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            String[] stackLines = sw.toString().split("\n");
            for (String line : stackLines) {
                logToWriter(testContextName, "        " + line);
            }
        }
        logToWriter(testContextName, "");
        logToWriter(testContextName, "-----------------------------------------------------------");
        logToWriter(testContextName, "");
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String testContextName = result.getTestContext().getName();
        String testName = getTestName(result);
        
        logToWriter(testContextName, "<<< TEST SKIPPED: " + testName);
        logToWriter(testContextName, "    End Time: " + dateFormat.format(new Date(result.getEndMillis())));
        
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            logToWriter(testContextName, "    Skip Reason: " + throwable.getMessage());
        }
        logToWriter(testContextName, "");
        logToWriter(testContextName, "-----------------------------------------------------------");
        logToWriter(testContextName, "");
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String testContextName = result.getTestContext().getName();
        String testName = getTestName(result);
        logToWriter(testContextName, "<<< TEST FAILED (within success percentage): " + testName);
        logToWriter(testContextName, "");
    }
    
    @Override
    public void onStart(ITestContext context) {
        String testName = context.getName();
        String logFileName = context.getCurrentXmlTest().getParameter("loggerFileName");
        
        if (logFileName == null || logFileName.trim().isEmpty()) {
            logFileName = null;
        }
        
        if (logFileName != null) {
            try {
                String testLogPath = "src/logs/" + logFileName;
                PrintWriter testWriter = new PrintWriter(new BufferedWriter(new FileWriter(testLogPath, false)), true);
                
                testLogWriters.put(testName, testWriter);
                testLogPaths.put(testName, testLogPath);
                
                logToTest(testName, "================================================================================");
                logToTest(testName, "TEST STARTED: " + testName);
                logToTest(testName, "Start Time: " + dateFormat.format(context.getStartDate()));
                logToTest(testName, "Total Test Methods: " + context.getAllTestMethods().length);
                logToTest(testName, "================================================================================");
                logToTest(testName, "");
                
                logger.info("Created test log file: " + testLogPath);
            } catch (IOException e) {
                logger.error("Failed to create test log file for: " + testName, e);
            }
        } else {
            logToSuite("================================================================================");
            logToSuite("TEST CONTEXT STARTED: " + testName);
            logToSuite("Start Time: " + dateFormat.format(context.getStartDate()));
            logToSuite("Total Test Methods: " + context.getAllTestMethods().length);
            logToSuite("================================================================================");
            logToSuite("");
        }
    }
    
    @Override
    public void onFinish(ITestContext context) {
        String testName = context.getName();
        PrintWriter testWriter = testLogWriters.get(testName);
        
        if (testWriter != null) {
            logToTest(testName, "");
            logToTest(testName, "================================================================================");
            logToTest(testName, "TEST COMPLETED: " + testName);
            logToTest(testName, "End Time: " + dateFormat.format(context.getEndDate()));
            logToTest(testName, "Passed Tests: " + context.getPassedTests().size());
            logToTest(testName, "Failed Tests: " + context.getFailedTests().size());
            logToTest(testName, "Skipped Tests: " + context.getSkippedTests().size());
            logToTest(testName, "================================================================================");
            
            testWriter.flush();
            testWriter.close();
            
            String logPath = testLogPaths.get(testName);
            logger.info("Test logs written to: " + logPath);
            
            testLogWriters.remove(testName);
            testLogPaths.remove(testName);
        } else {
            logToSuite("");
            logToSuite("================================================================================");
            logToSuite("TEST CONTEXT COMPLETED: " + testName);
            logToSuite("End Time: " + dateFormat.format(context.getEndDate()));
            logToSuite("Passed Tests: " + context.getPassedTests().size());
            logToSuite("Failed Tests: " + context.getFailedTests().size());
            logToSuite("Skipped Tests: " + context.getSkippedTests().size());
            logToSuite("================================================================================");
            logToSuite("");
        }
    }
    
    private String getTestName(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Object[] parameters = result.getParameters();
        
        if (parameters != null && parameters.length > 0) {
            StringBuilder paramStr = new StringBuilder();
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i] instanceof Map) {
                    Map<String, String> dataMap = (Map<String, String>) parameters[i];
                    String testCase = dataMap.get("TestCase");
                    if (testCase != null) {
                        paramStr.append(testCase);
                    } else {
                        paramStr.append("Data[").append(i).append("]");
                    }
                } else {
                    paramStr.append(parameters[i].toString());
                }
                if (i < parameters.length - 1) {
                    paramStr.append(", ");
                }
            }
            if (paramStr.length() > 0) {
                testName = testName + " [" + paramStr + "]";
            }
        }
        
        return testName;
    }
    
    private void logToWriter(String testContextName, String message) {
        PrintWriter testWriter = testLogWriters.get(testContextName);
        if (testWriter != null) {
            logToTest(testContextName, message);
        } else {
            logToSuite(message);
        }
    }
    
    private void logToTest(String testContextName, String message) {
        PrintWriter testWriter = testLogWriters.get(testContextName);
        if (testWriter != null) {
            testWriter.println(message);
        }
    }
    
    private static void logToSuite(String message) {
        if (suiteLogWriter != null) {
            suiteLogWriter.println(message);
        }
    }
}
