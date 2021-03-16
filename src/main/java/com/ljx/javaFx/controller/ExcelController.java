package com.ljx.javaFx.controller;

import com.ljx.javaFx.service.ExcelParseService;
import com.ljx.javaFx.utils.FileUtil;
import com.ljx.javaFx.utils.MessageUtil;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author lijx
 * @date 2021/1/12 - 16:44
 */
public class ExcelController {

    private ExcelParseService service = new ExcelParseService();

    private static Stage stage;

    @FXML
    private TextField sheetIndex;

    @FXML
    private TextField startRowIndex;

    private File sourceExcel;

    private File targetDir;

    @FXML
    private CheckBox dingCheckBox;

    @FXML
    private CheckBox dingGeneIndex;

    @FXML
    private CheckBox listCheckBox;

    @FXML
    private CheckBox listGeneIndex;

    @FXML
    private CheckBox detailCheckBox;

    @FXML
    private CheckBox detailGeneIndex;

    @FXML
    private CheckBox onePictureCheckBox;

    @FXML
    private CheckBox onePictureGeneIndex;

    @FXML
    private CheckBox contentCheckBox;

    @FXML
    private void sourceExcel() {
        //文件选择器
        File file = new FileChooser().showOpenDialog(stage);
        if (file == null || !file.exists() || !file.getName().endsWith(".xlsx")) {
            MessageUtil.alert("温馨提示", "         必须选择一个xlsx格式的文件");
        }
        sourceExcel = file;
    }

    @FXML
    private void targetDir() {
        //目录选择器
        File dir = new DirectoryChooser().showDialog(stage);
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            MessageUtil.alert("温馨提示", "         请选择正确的目录");
        }
        targetDir = dir;
    }


    public static void setStage(Stage stage) {
        ExcelController.stage = stage;
    }

    private String getSheetIndexText() {
        //TextField只能通过这种方式设置值和取值
        return sheetIndex.textProperty().get();
    }

    private String getStartRowIndexText() {
        //TextField只能通过这种方式设置值和取值
        return startRowIndex.textProperty().get();
    }

    @FXML
    public synchronized void generateAll() {
        if (sourceExcel == null || targetDir == null) {
            MessageUtil.alert("温馨提示", "        请先选择源文件和生成文件夹");
            return;
        }
        String sheetIndex = "".equals(getSheetIndexText()) ? "0" : getSheetIndexText();
        String startRowIndex = "".equals(getStartRowIndexText()) ? "0" : getStartRowIndexText();
        Path outputDir = Paths.get(targetDir.getAbsolutePath(), "输出目录");
        try {
            FileUtil.delete(outputDir);
            Files.createDirectory(outputDir);
        } catch (IOException e) {
            e.printStackTrace();
            MessageUtil.alert("温馨提示", "创建目录发生异常，e：" + e.toString());
            return;
        }
        try {
            List<List<String>> data = service.parseOriginExcel(sourceExcel, Integer.parseInt(sheetIndex), Integer.parseInt(startRowIndex));
            if (dingCheckBox.isSelected()) {
                service.geneExcelFile(data, outputDir, "ding", dingGeneIndex.isSelected());
            }
            if (listCheckBox.isSelected()) {
                service.geneExcelFile(data, outputDir, "list", listGeneIndex.isSelected());
            }
            if (detailCheckBox.isSelected()) {
                service.geneExcelFile(data, outputDir, "detail", detailGeneIndex.isSelected());
            }
            if (onePictureCheckBox.isSelected()) {
                service.geneExcelFile(data, outputDir, "onePicture", onePictureGeneIndex.isSelected());
            }
            if (contentCheckBox.isSelected()) {
                service.generateTxtFile(data, outputDir.toFile());
            }
            MessageUtil.alert("温馨提示", "        生成完毕，请在\"" + outputDir.toAbsolutePath().toString() + "\"查看");
        } catch (Exception e) {
            MessageUtil.alert("温馨提示", "生成文件发生异常，e：" + e.toString());
            e.printStackTrace();
        } finally {
            sourceExcel = null;
            targetDir = null;
        }
    }
}
