package org.example;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * * @Description : TODO导入excel
 * <p>
 * * @date : Mar 31, 2018 5:17:14 PM
 * <p>
 */

public class ExcelImport {

    public static void main(String args[]) {

        ExcelImport excelImport = new ExcelImport();

        try {

            excelImport.importExcelAction();

        } catch (Exception e) {

// TODO Auto-generated catch block

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

}

