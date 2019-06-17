package com.ljx.javaFx.controller;

import com.ljx.javaFx.service.TimeStatisticsService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * @author lijx
 * @date 2019/4/11 - 9:27
 */

//可以不实现Initializable接口
public class TimeStatisticsController {
    @FXML
    private TextArea timeStatisticsInputTextArea;

    @FXML
    private TextField timeStatisticsResultTextField;

    private TimeStatisticsService timeStatisticsService = new TimeStatisticsService();

    private static Stage stage;

    @FXML
    private void emptyText(ActionEvent event) {
        timeStatisticsInputTextArea.clear();
    }

    @FXML
    private void timeStatistics() {
        String inputText = getInputText();
        setText(timeStatisticsService.timeStatistics(inputText));
    }

    @FXML
    private void resetTime() {
        timeStatisticsResultTextField.textProperty().setValue("00:00:00");
        timeStatisticsService.setTotalTime("00:00:00");
    }
    @FXML
    private void dirStatistics(){
        //文件选择器
//        File file = fileChooser.showOpenDialog(stage);
        //目录选择器
        File dir = new DirectoryChooser().showDialog(stage);
        String time=timeStatisticsService.dirTimeStatistics(dir);
        timeStatisticsResultTextField.textProperty().set(time);
    }

    private String getInputText() {
        return timeStatisticsInputTextArea.textProperty().getValue();
        //对于TextArea来说，这两种方式都可以取值
//        return inputTextArea.getText();
    }

    private void setText(String str) {
        //TextField只能通过这种方式设置值和取值
        timeStatisticsResultTextField.textProperty().setValue(timeStatisticsService.getTotalTime());
    }

    public static void setStage(Stage stage) {
        TimeStatisticsController.stage = stage;
    }
}
