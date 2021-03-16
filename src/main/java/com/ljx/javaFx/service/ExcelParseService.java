package com.ljx.javaFx.service;

import com.ljx.javaFx.utils.ExcelUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author lijx
 * @date 2021/1/6 - 17:10
 * todo 支持指定单元格默认值
 */
public class ExcelParseService {

    private HashMap<String, Integer[]> readIndexMap = new HashMap<>();
    private HashMap<String, Integer[]> writeIndexMap = new HashMap<>();
    private HashMap<String, String> templatePathMap = new HashMap<>();
    private HashMap<String, String> fileNameMap = new HashMap<>();

    {
        initiateMap();
    }

    /**
     * 通用的生成excel的流程，会解析源excel
     *
     * @param sourceExcelFile
     * @param target
     * @param fileType
     * @param needIndex
     */
    public void geneExcelFile(File sourceExcelFile, File target, String fileType, boolean needIndex) {
        if (!fileValidate(sourceExcelFile, target)) {
            System.out.println("文件非法");
            return;
        }
        if (!fileNameMap.containsKey(fileType)) {
            System.out.println("不支持的类型");
            return;
        }
        List<List<String>> data = parseOriginExcel(sourceExcelFile);
        String newPath = target.getAbsolutePath() + File.separator + fileNameMap.get(fileType);
        geneExcel(newPath, templatePathMap.get(fileType), data, readIndexMap.get(fileType), writeIndexMap.get(fileType), needIndex);
    }

    /**
     * 通用的生成excel的流程，通过list数据来生成
     *
     * @param sourceExcelData
     * @param target
     * @param fileType
     * @param needIndex
     */
    public void geneExcelFile(List<List<String>> sourceExcelData, Path target, String fileType, boolean needIndex) {
        if (sourceExcelData == null || sourceExcelData.size() == 0) {
            System.out.println("源数据为空");
            return;
        }
        if (!fileNameMap.containsKey(fileType)) {
            System.out.println("不支持的类型");
            return;
        }
        String newPath = target.toAbsolutePath().toString() + File.separator + fileNameMap.get(fileType);
        geneExcel(newPath, templatePathMap.get(fileType), sourceExcelData, 0, 1, readIndexMap.get(fileType), writeIndexMap.get(fileType), needIndex);
    }

    /**
     * 默认方式解析源excel
     *
     * @param excelFile
     * @return
     */
    public List<List<String>> parseOriginExcel(File excelFile) {
        return ExcelUtil.readExcel(excelFile, null, null);
    }

    /**
     * 解析源excel,可指定sheet也和起始行
     *
     * @param excelFile
     * @param sheetIndex
     * @param startRow
     * @return
     */
    public List<List<String>> parseOriginExcel(File excelFile, Integer sheetIndex, Integer startRow) {
        return ExcelUtil.readExcel(excelFile, sheetIndex, startRow);
    }

    private boolean fileValidate(File sourceExcelFile, File target) {
        if (sourceExcelFile == null || !sourceExcelFile.getName().endsWith(".xlsx")) {
            return false;
        }
        if (target == null || !target.isDirectory()) {
            return false;
        }
        return true;
    }

    private boolean geneExcel(String filePath, String templatePath, List<List<String>> fileData, Integer[] readIndex, Integer[] writeIndex, boolean needIndex) {
        Path path = Paths.get(filePath);
        URL template = ExcelParseService.class.getResource(templatePath);
        try {
            Files.deleteIfExists(path);
            Files.copy(Paths.get(template.toURI()), path);
            ExcelUtil.overwriteExcel(new File(filePath), null, null, fileData, readIndex, writeIndex, needIndex);
        } catch (Exception e) {
            System.out.println("创建文件异常，e: " + e.getMessage());
            return false;
        }
        return true;
    }

    private boolean geneExcel(String filePath, String templatePath, List<List<String>> fileData, Integer sheetIndex, Integer startRow, Integer[] readIndex, Integer[] writeIndex, boolean needIndex) {
        Path path = Paths.get(filePath);
        InputStream in = ExcelParseService.class.getResourceAsStream(templatePath);
        try {
            Files.deleteIfExists(path);
            Files.copy(in, path);
            ExcelUtil.overwriteExcel(new File(filePath), sheetIndex, startRow, fileData, readIndex, writeIndex, needIndex);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("创建文件异常，e: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 解析配置文件
     */
    private void initiateMap() {
        InputStream stream = this.getClass().getResourceAsStream("/properties/excelConfig.properties");
        Properties properties = new Properties();
        try {
            //注意，此处使用InputStreamReader防止乱码
            properties.load(new InputStreamReader(stream, "GBK"));
        } catch (IOException e) {
            System.out.println("读取配置文件异常，e：" + e.getMessage());
            return;
        }
        Set<String> strings = properties.stringPropertyNames();
        for (String key : strings) {
            if (key.startsWith("readIndex")) {
                String configName = key.substring(key.indexOf(".") + 1);
                readIndexMap.put(configName, parseString(properties.getProperty(key).trim()));
                continue;
            }
            if (key.startsWith("writeIndex")) {
                String configName = key.substring(key.indexOf(".") + 1);
                writeIndexMap.put(configName, parseString(properties.getProperty(key).trim()));
                continue;
            }
            if (key.startsWith("templatePath")) {
                String configName = key.substring(key.indexOf(".") + 1);
                templatePathMap.put(configName, properties.getProperty(key).trim());
                continue;
            }
            if (key.startsWith("filename")) {
                String configName = key.substring(key.indexOf(".") + 1);
                fileNameMap.put(configName, properties.getProperty(key).trim());
            }
        }
    }

    private Integer[] parseString(String s) {
        if (s == null | s.length() == 0) {
            return new Integer[]{};
        }
        String[] split = s.split(",");
        Integer[] result = new Integer[split.length];
        for (int i = 0; i < split.length; i++) {
            result[i] = new Integer(split[i].trim());
        }
        return result;
    }

    /**
     * 生成文本文档
     *
     * @param sourceExcelFile
     * @param targetPath
     * @return
     */
    public boolean generateTxtFile(File sourceExcelFile, File targetPath) {
        if (!fileValidate(sourceExcelFile, targetPath)) {
            System.out.println("文件非法");
            return false;
        }
        if (!targetPath.isDirectory()) {
            return false;
        }
        List<List<String>> data = parseOriginExcel(sourceExcelFile);
        return doGenerateTxtFile(data, targetPath);
    }

    /**
     * 通过list数据生成文本文档
     *
     * @param data
     * @param targetPath
     * @return
     */
    public boolean generateTxtFile(List<List<String>> data, File targetPath) {
        if (data == null || data.size() == 0) {
            System.out.println("源数据为空");
            return false;
        }
        if (!targetPath.isDirectory()) {
            return false;
        }
        return doGenerateTxtFile(data, targetPath);
    }

    private boolean doGenerateTxtFile(List<List<String>> data, File targetPath) {
        String newPath = targetPath.getAbsolutePath() + File.separator + "发版内容.txt";
        StringBuffer buffer = new StringBuffer();
        String line = System.getProperty("line.separator");
        buffer.append(line);
        for (int i = 0; i < data.size(); i++) {
            List<String> row = data.get(i);
            buffer.append(i + 1).append("、任务类型：").append(row.get(2)).append("。天梯标题：").append(row.get(4)).append("。更新内容：").append(row.get(6)).append("。问题场景：").append(row.get(11)).append("。影响范围：").append(row.get(22)).append("。").append(line).append(line);
        }
        buffer.append(line);
        for (int i = 0; i < data.size(); i++) {
            List<String> row = data.get(i);
            buffer.append(i + 1).append("、天梯id：").append(row.get(3)).append("。任务类型：").append(row.get(2)).append("。天梯标题：").append(row.get(4)).append("。版本号：暂无。服务名称：").append(parseServiceName(row.get(8))).append("。更新内容：").append(row.get(6)).append("。问题场景：").append(row.get(11)).append("。影响范围：").append(row.get(22)).append("。").append(line).append(line);
        }
        try {
            Files.write(Paths.get(newPath), buffer.toString().getBytes());
        } catch (IOException e) {
            System.out.println("发生异常，e: " + e.getMessage());
            return false;
        }
        return true;
    }

    private String[] filterString = {"\\s", ",", "，", "。", ":", "："};

    private Pattern featurePattern = Pattern.compile("feature_\\d{4,6}_\\d{8,9}");

    private Pattern releasePattern = Pattern.compile("release_\\d{8}");

    /**
     * 使用了正则来替换指定字符串，java的正则有一些坑，请注意
     * @param origin
     * @return
     */
    public String parseServiceName(String origin) {
        String str = origin;
        if (str == null || str.length() == 0) {
            return "";
        }
        for (String s : filterString) {
            str = str.replaceAll(s, "");
        }
        str = featurePattern.matcher(str).replaceAll("、");
        str = releasePattern.matcher(str).replaceAll("、");
        if (str.endsWith("、")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }
}
