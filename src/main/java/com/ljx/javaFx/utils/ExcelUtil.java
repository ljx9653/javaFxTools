package com.ljx.javaFx.utils;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用poi和easyExcel来进行excel读写
 *
 * @author lijx
 * @date 2021/1/8 - 16:44
 */
public class ExcelUtil {
    /**
     * poi获取sheet内的一个单元格的内容
     *
     * @param path
     * @param sheetIndex
     * @param rowIndex
     * @param cellIndex
     * @return
     */
    public static String readOneCell(String path, Integer sheetIndex, Integer rowIndex, Integer cellIndex) {
        Workbook excel = null;
        try {
            excel = new XSSFWorkbook(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (sheetIndex == null) {
            sheetIndex = new Integer(0);
        }
        Sheet sheet = excel.getSheetAt(sheetIndex);
        Cell cell = sheet.getRow(rowIndex).getCell(cellIndex);
        return cell.toString();
    }

    private static Integer defaultSheetIndex = 0;

    private static Integer skipHeadRowIndex = 1;

    private static Integer defaultRowIndex = 0;


    /**
     * 使用poi来对已有excel进行覆写，不会删除原文件，可指定sheet页和起始行，数据可跳列读取及写入
     *
     * @param excelFile
     * @param sheetIndex
     * @param startRow
     * @param data
     * @param readIndex
     * @param writeIndex
     */
    public static void overwriteExcel(File excelFile, Integer sheetIndex, Integer startRow, List<List<String>> data, Integer[] readIndex, Integer[] writeIndex, boolean generateIndex) {
        Workbook excel = null;
        try {
            FileInputStream fs = new FileInputStream(excelFile);
            excel = new XSSFWorkbook(fs);
        } catch (IOException e) {
            return;
        }
        boolean indexFlag = generateIndex;
        if (sheetIndex == null) {
            sheetIndex = defaultSheetIndex;
        }
        if (startRow == null) {
            //默认跳过首行开始写
            startRow = skipHeadRowIndex;
        }
        if (data == null || data.size() == 0) {
            return;
        }
        OutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(excelFile);
        } catch (FileNotFoundException e) {
            return;
        }
        CellStyle cellStyle = setupCellStyle(excel);
        Sheet sheet = excel.getSheetAt(sheetIndex);
        //先清空要写的行
        clearRows(sheet, startRow);
        for (int i = 0, k = startRow; i < data.size(); i++, k++) {
            List<String> rowData = data.get(i);
            Row row = sheet.createRow(k);
            if (indexFlag == true) {
                createCell(row, 0, (i + 1) + "", true, cellStyle);
            }
            if (readIndex != null) {
                if (writeIndex != null && writeIndex.length != 0) {
                    for (int j = 0; j < readIndex.length; j++) {
                        if (j >= writeIndex.length) {
                            break;
                        }
                        int index = readIndex[j];
                        //防止下标越界
                        if (index >= rowData.size()) {
                            continue;
                        }
                        createCell(row, writeIndex[j], rowData.get(index) == null ? "" : rowData.get(index), false, null);
                    }
                } else {
                    for (int j = 0; j < readIndex.length; j++) {
                        int index = readIndex[j];
                        if (index >= rowData.size()) {
                            continue;
                        }
                        //注意，不能用getCell方法，会导致空指针。单元格的值不能为null，否则会报错
                        if (indexFlag == true) {
                            createCell(row, j + 1, rowData.get(index) == null ? "" : rowData.get(index), true, cellStyle);
                        } else {
                            createCell(row, j, rowData.get(index) == null ? "" : rowData.get(index), true, cellStyle);
                        }
                    }
                }
            } else {
                for (int j = 0; j < rowData.size(); j++) {
                    if (indexFlag == true) {
                        createCell(row, j + 1, rowData.get(j) == null ? "" : rowData.get(j), true, cellStyle);
                    } else {
                        createCell(row, j, rowData.get(j) == null ? "" : rowData.get(j), true, cellStyle);
                    }
                }

            }
        }
        //跳列写出时，需要设置全边框，否则很难看
        if (writeIndex != null && writeIndex.length != 0) {
            fillAllBorder(sheet, startRow, data.size(), cellStyle);
        }
        try {
            excel.write(fileOut);
        } catch (FileNotFoundException e) {
            System.err.println("文件路径无效");
        } catch (IOException e1) {
            System.err.println("写出Excel文件失败");
        }
    }

    private static void clearRows(Sheet sheet, Integer startRow) {
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum >= startRow) {
            /*//删除startRow和endRow之间的行，下方单元格上移，第三个参数代表要删除的行数，-1表示全部
            sheet.shiftRows(startRow, lastRowNum, -1);*/
            //只清除行的内容，不删除行。不能采用上面那种方法删除行，会导致多次重复写excel时发生异常
            for (int i = startRow; i < lastRowNum; i++) {
                //拿到的行可能为null
                if (sheet.getRow(i) == null) {
                    continue;
                }
                sheet.removeRow(sheet.getRow(i));
            }
        }
    }

    private static void fillAllBorder(Sheet sheet, Integer startRow, Integer cellNum, CellStyle cellStyle) {
        Row head = sheet.getRow(0);
        if (head != null) {
            short lastCellNum = head.getLastCellNum();
            for (int i = startRow; i < startRow + cellNum; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < lastCellNum; j++) {
                    if (row == null) {
                        continue;
                    }
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        cell.setCellStyle(cellStyle);
                    } else {
                        row.createCell(j).setCellStyle(cellStyle);
                    }
                }
            }
        }
    }

    private static void createCell(Row row, Integer index, String value, boolean setBorder, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        if (setBorder == true) {
            //设置全边框
            cell.setCellStyle(style);
        }
    }

    private static CellStyle setupCellStyle(Workbook excel) {
        CellStyle cellStyle = excel.createCellStyle();
        //设置边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        //自动换行
        cellStyle.setWrapText(true);
        //居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }

    /**
     * 使用EasyExcel读取Excel，并将数据转换为 List<List<String>>
     *
     * @param excelFile
     * @param sheetIndex
     * @param startRow
     * @return
     */
    public static List<List<String>> readExcel(File excelFile, Integer sheetIndex, Integer startRow) {
        if (sheetIndex == null) {
            sheetIndex = defaultSheetIndex;
        }
        if (startRow == null) {
            //EasyExcel默认跳过首行，此处不跳过
            startRow = defaultRowIndex;
        }
        MyListener myListener = new MyListener();
        //异步无实体类的读取
        EasyExcelFactory.read(excelFile, myListener).sheet(sheetIndex).headRowNumber(startRow).doRead();
        return myListener.getDatas();
    }

    /**
     * 继承自异步监听器
     * 默认情况下EasyExcel读取excel后放回的数据格式为List<LinkedHashMap<Integer, String>>，此类负责转换将数据为List<List<String>>
     */
    private static class MyListener extends AnalysisEventListener {

        private List<List<String>> datas = new ArrayList<>();

        @Override
        public void invoke(Object data, AnalysisContext context) {
            if (data instanceof LinkedHashMap) {
                Map<Integer, String> dataMap = (LinkedHashMap<Integer, String>) data;
                ArrayList<String> list = new ArrayList<>();
                dataMap.forEach((x, y) -> list.add(x, y));
                datas.add(list);
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
        }

        public List<List<String>> getDatas() {
            return datas;
        }
    }
}
