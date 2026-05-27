package com.saucedemo.automation.utils;

import org.testng.annotations.DataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestDataProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(TestDataProvider.class);
    
    @DataProvider(name = "excelDataProvider")
    public static Iterator<Object[]> excelDataProvider(String testDataParam) {
        List<Object[]> dataList = new ArrayList<>();
        
        try {
            ExcelReader reader = ExcelReader.fromParameter(testDataParam);
            List<Map<String, String>> allData = reader.getAllData();
            
            for (Map<String, String> rowData : allData) {
                dataList.add(new Object[]{rowData});
            }
            
            reader.close();
            logger.info("DataProvider loaded {} test data rows", dataList.size());
        } catch (Exception e) {
            logger.error("Error loading test data from: {}", testDataParam, e);
            throw new RuntimeException("Error loading test data: " + testDataParam, e);
        }
        
        return dataList.iterator();
    }
    
    @DataProvider(name = "loginData")
    public static Iterator<Object[]> loginData() {
        String testDataPath = "./src/main/resources/testdata/LoginData.xlsx:LoginTests";
        return excelDataProvider(testDataPath);
    }
    
    @DataProvider(name = "cartData")
    public static Iterator<Object[]> cartData() {
        String testDataPath = "./src/main/resources/testdata/CartData.xlsx:CartTests";
        return excelDataProvider(testDataPath);
    }
    
    @DataProvider(name = "checkoutData")
    public static Iterator<Object[]> checkoutData() {
        String testDataPath = "./src/main/resources/testdata/CheckoutData.xlsx:CheckoutTests";
        return excelDataProvider(testDataPath);
    }
    
    public static Map<String, String> getTestData(String testDataParam, String testCaseName) {
        ExcelReader reader = ExcelReader.fromParameter(testDataParam);
        Map<String, String> data = reader.getTestCaseData(testCaseName);
        reader.close();
        return data;
    }
    
    public static List<Map<String, String>> getFilteredTestData(ExcelReader reader, String testDataSets) {
        if (testDataSets == null || testDataSets.trim().isEmpty()) {
            return reader.getAllData();
        }
        
        if ("ALL".equalsIgnoreCase(testDataSets.trim())) {
            return reader.getAllData();
        }
        
        String[] testCases = testDataSets.split(",");
        List<Map<String, String>> filteredData = new java.util.ArrayList<>();
        
        for (String testCase : testCases) {
            Map<String, String> data = reader.getTestCaseData(testCase.trim());
            if (data != null && !data.isEmpty()) {
                filteredData.add(data);
            }
        }
        
        return filteredData;
    }
}
