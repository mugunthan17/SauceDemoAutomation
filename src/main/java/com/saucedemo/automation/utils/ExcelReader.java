package com.saucedemo.automation.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {
    
    private static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);
    private Workbook workbook;
    private Sheet sheet;
    private String filePath;
    
    public ExcelReader(String filePath, String sheetName) {
        this.filePath = filePath;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                logger.error("Sheet '{}' not found in file: {}", sheetName, filePath);
                throw new RuntimeException("Sheet '" + sheetName + "' not found in file: " + filePath);
            }
            logger.info("Excel file loaded successfully: {} - Sheet: {}", filePath, sheetName);
        } catch (IOException e) {
            logger.error("Error loading Excel file: {}", filePath, e);
            throw new RuntimeException("Error loading Excel file: " + filePath, e);
        }
    }
    
    public static ExcelReader fromParameter(String testDataParam) {
        if (testDataParam == null || !testDataParam.contains(":")) {
            throw new RuntimeException("Invalid testData parameter format. Expected: 'filepath:sheetname'");
        }
        String[] parts = testDataParam.split(":");
        String filePath = parts[0];
        String sheetName = parts[1];
        return new ExcelReader(filePath, sheetName);
    }
    
    public List<Map<String, String>> getAllData() {
        List<Map<String, String>> dataList = new ArrayList<>();
        
        if (sheet == null) {
            logger.warn("Sheet is null, returning empty data");
            return dataList;
        }
        
        int rowCount = sheet.getLastRowNum();
        if (rowCount < 1) {
            logger.warn("No data rows found in sheet");
            return dataList;
        }
        
        Row headerRow = sheet.getRow(0);
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(getCellValueAsString(cell));
        }
        
        for (int i = 1; i <= rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            Map<String, String> rowData = new HashMap<>();
            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j);
                String value = (cell != null) ? getCellValueAsString(cell) : "";
                rowData.put(headers.get(j), value);
            }
            dataList.add(rowData);
        }
        
        logger.info("Read {} data rows from sheet", dataList.size());
        return dataList;
    }
    
    public List<Map<String, String>> getDataByColumnValue(String columnName, String columnValue) {
        List<Map<String, String>> allData = getAllData();
        List<Map<String, String>> filteredData = new ArrayList<>();
        
        for (Map<String, String> row : allData) {
            if (columnValue.equals(row.get(columnName))) {
                filteredData.add(row);
            }
        }
        
        logger.info("Found {} rows where {}={}", filteredData.size(), columnName, columnValue);
        return filteredData;
    }
    
    public Map<String, String> getTestCaseData(String testCaseName) {
        List<Map<String, String>> data = getDataByColumnValue("TestCase", testCaseName);
        if (data.isEmpty()) {
            logger.warn("No data found for test case: {}", testCaseName);
            return new HashMap<>();
        }
        return data.get(0);
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numValue = cell.getNumericCellValue();
                    if (numValue == (long) numValue) {
                        return String.valueOf((long) numValue);
                    } else {
                        return String.valueOf(numValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }
    
    public String getCellData(int rowNum, int colNum) {
        Row row = sheet.getRow(rowNum);
        if (row == null) return "";
        Cell cell = row.getCell(colNum);
        return getCellValueAsString(cell);
    }
    
    public int getRowCount() {
        return sheet.getLastRowNum();
    }
    
    public int getColumnCount() {
        Row row = sheet.getRow(0);
        return (row != null) ? row.getLastCellNum() : 0;
    }
    
    public void close() {
        try {
            if (workbook != null) {
                workbook.close();
                logger.info("Excel workbook closed");
            }
        } catch (IOException e) {
            logger.error("Error closing workbook", e);
        }
    }
}
