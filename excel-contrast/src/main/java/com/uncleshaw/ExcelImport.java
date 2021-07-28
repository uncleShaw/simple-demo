package com.uncleshaw;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @Description: Excel文件内容对比
 * @auther: shaw
 * @date: 2020/6/11 18:48
 */
public class ExcelImport {
    
    public static void main(String args[]) {
        
        ExcelImport excelImport = new ExcelImport();
        
        try {
            
            excelImport.importExcelAction();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    //导入Excel数据
    public void importExcelAction() throws Exception {
        Map<String, List<String>> stringListMap = new HashMap<>();
        //文件路径
        String oldFilePath = "C:\\Users\\yichen.xiao\\Desktop\\lang_EN-20200611";
        String newFilePath = "C:\\Users\\yichen.xiao\\Desktop\\2.5.0英文";
        File[] oldFiles = new File(oldFilePath).listFiles();
        File[] newFiles = new File(newFilePath).listFiles();
        FileInputStream fileInputStream = null;
        for (File file : oldFiles) {
            fileInputStream = new FileInputStream(file);
            Workbook wb = new HSSFWorkbook(fileInputStream);
            Sheet sheetAt = wb.getSheetAt(0);
            Row row = sheetAt.getRow(0);
            
            List<String> stringList = new ArrayList<>();
            String fileName = file.getName();
            for (int cellNum = row.getFirstCellNum(), lcell = row.getLastCellNum(); cellNum <= lcell; cellNum++) {
                Cell rowCell = row.getCell(cellNum);
                if (!(rowCell == null || rowCell.toString().equals(""))) {
                    stringList.add(rowCell.toString());
                }
            }
            Collections.sort(stringList);
            stringListMap.put(fileName, stringList);
            fileInputStream.close();
        }
        
        for (File file : newFiles) {
            fileInputStream = new FileInputStream(file);
            Workbook wb = new HSSFWorkbook(fileInputStream);
            Sheet sheetAt = wb.getSheetAt(0);
            Row row = sheetAt.getRow(0);
            
            List<String> stringList = new ArrayList<>();
            String fileName = file.getName();
            for (int cellNum = row.getFirstCellNum(), lcell = row.getLastCellNum(); cellNum <= lcell; cellNum++) {
                Cell rowCell = row.getCell(cellNum);
                if (!(rowCell == null || rowCell.toString().equals(""))) {
                    stringList.add(rowCell.toString());
                }
            }
            Collections.sort(stringList);
            if (stringListMap.containsKey(fileName)) {
                List<String> stringList1 = stringListMap.get(fileName);
                int i = stringList1.size() - stringList.size();
                if (i == 0) {
                    if (!stringList1.containsAll(stringList)) {
                        System.out.println(String.format("[%s]字段内容不一样：", fileName));
                        System.out.println("新:" + stringList.toString());
                        System.out.println("老:" + stringList1.toString());
                    }
                } else {
                    System.out.println(String.format("[%s]长度不一样：", fileName));
                    System.out.println("新:" + stringList.toString());
                    System.out.println("老:" + stringList1.toString());
                }
            } else {
                stringListMap.put(fileName, stringList);
            }
            fileInputStream.close();
        }
        
    }
    
    /**
     * @Description: 获取指定行的数据，过滤空格
     * @param: [filePath, rowNum] 文件地址、指定行
     * @return: java.util.List<java.lang.String>
     * @auther: shaw
     * @date: 2020/6/11 21:42
     */
    public List<String> getExcelRow(String filePath, int rowNum) throws Exception {
        List<String> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("找不到文件！");
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        Workbook wb = new HSSFWorkbook(fileInputStream);
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.getRow(rowNum);
        for (int cellNum = row.getFirstCellNum(), lcell = row.getLastCellNum(); cellNum <= lcell; cellNum++) {
            Cell rowCell = row.getCell(cellNum);
            if (!(rowCell == null || rowCell.toString().equals(""))) {
                list.add(rowCell.toString());
            }
        }
        fileInputStream.close();
        return list;
    }
    
    
    /**
     * @Description: 原文件指定行的单元格数据是否是目标文件指定行的子集，忽略空格
     * @param: [sourceFilePath, targetFilePath, rowNum] 原文件、目标文件、指定行
     * @return: java.util.List<java.lang.String> 原返回文件中的指定行的差异数据
     * @auther: shaw
     * @date: 2020/6/11 22:05
     */
    public List<String> compareExcelRow(String sourceFilePath, String targetFilePath, int rowNum) throws Exception {
        List<String> sourceRow = getExcelRow(sourceFilePath, rowNum);
        List<String> targetRow = getExcelRow(targetFilePath, rowNum);
        sourceRow.retainAll(targetRow);
        return sourceRow;
        
    }
    
}

