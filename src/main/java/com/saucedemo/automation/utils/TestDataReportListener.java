package com.saucedemo.automation.utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.util.Map;

public class TestDataReportListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        Object[] parameters = result.getParameters();
        
        if (parameters != null && parameters.length > 0) {
            StringBuilder dataLog = new StringBuilder();
            dataLog.append("<div style='margin:5px 0; padding:5px; background-color:#f0f8ff; border-left:3px solid #1e90ff;'>");
            dataLog.append("<b>Test Data:</b><br/>");
            
            for (int i = 0; i < parameters.length; i++) {
                Object param = parameters[i];
                
                if (param instanceof Map) {
                    Map<String, String> dataMap = (Map<String, String>) param;
                    dataLog.append("<table border='1' cellpadding='3' cellspacing='0' style='border-collapse:collapse; font-size:13px;'>");
                    dataLog.append("<tr style='background-color:#1e90ff; color:white;'><th>Field</th><th>Value</th></tr>");
                    
                    for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                        String value = entry.getValue();
                        dataLog.append("<tr><td>").append(entry.getKey()).append("</td>");
                        dataLog.append("<td>").append(value).append("</td></tr>");
                    }
                    dataLog.append("</table>");
                } else {
                    dataLog.append("Parameter ").append(i + 1).append(": ");
                    dataLog.append("<code>").append(param.toString()).append("</code><br/>");
                }
            }
            
            dataLog.append("</div>");
            Reporter.log(dataLog.toString(), true);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logTestResult(result, "PASSED", "#28a745");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logTestResult(result, "FAILED", "#dc3545");
        
        if (result.getThrowable() != null) {
            Reporter.log("<div style='margin:5px 0; padding:5px; background-color:#fff3cd; border-left:3px solid #ffc107;'>");
            Reporter.log("<b>Failure Reason:</b><br/>");
            Reporter.log("<pre style='color:#721c24; margin:0;'>" + result.getThrowable().getMessage() + "</pre>");
            Reporter.log("</div>", true);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logTestResult(result, "SKIPPED", "#ffc107");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logTestResult(result, "FAILED (within success %)", "#ff9800");
    }

    @Override
    public void onStart(ITestContext context) {
        Reporter.log("<h2 style='color:#1e90ff;'>Test Suite: " + context.getName() + "</h2>", true);
    }

    @Override
    public void onFinish(ITestContext context) {
        Reporter.log("<hr/>", true);
    }

    private void logTestResult(ITestResult result, String status, String color) {
        StringBuilder resultLog = new StringBuilder();
        resultLog.append("<div style='margin:5px 0; padding:5px; background-color:").append(color).append("20; border-left:3px solid ").append(color).append(";'>");
        resultLog.append("<b>Status: <span style='color:").append(color).append(";'>").append(status).append("</span></b>");
        resultLog.append(" | Duration: ").append(result.getEndMillis() - result.getStartMillis()).append(" ms");
        resultLog.append("</div>");
        Reporter.log(resultLog.toString(), true);
    }
}
