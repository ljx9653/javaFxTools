package com.ljx.javaFx.controller;

import com.ljx.javaFx.service.CourseJsonService;
import com.ljx.javaFx.utils.ClipboardUtil;
import com.ljx.javaFx.utils.TimeUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * @author lijx
 * @date 2019/4/15 - 11:14
 */
public class CourseJsonController {

    @FXML
    private TextField courseTabMessageTextField;

    @FXML
    private TextField courseTabTimeTextField;

    @FXML
    private TextArea courseTabInputTextArea;

    private CourseJsonService courseJsonService = new CourseJsonService();

    @FXML
    void convert(ActionEvent event) {
        String time = courseTabTimeTextField.textProperty().get().trim();
        System.out.println(time);
        String input = courseTabInputTextArea.textProperty().get();
        System.out.println(input);
        if ("".equals(time) || "".equals(input)) {
            courseTabMessageTextField.textProperty().setValue("请输入两个必填项");
            return;
        }
        if (!TimeUtil.validateTime(time)) {
            courseTabMessageTextField.textProperty().setValue("时间不合法");
            return;
        }
        String result = courseJsonService.convert(input, time);
        if ("error".equals(result)){
            courseTabInputTextArea.textProperty().setValue("");
            courseTabMessageTextField.textProperty().setValue("报文输入有误，请重新输入");
            return;
        }
        ClipboardUtil.setClipboardText(result);
        courseTabMessageTextField.textProperty().setValue("处理内容已复制到剪切板");
    }

    @FXML
    void emptyRequest() {
        courseTabInputTextArea.textProperty().setValue("");
        courseTabMessageTextField.textProperty().setValue("请输入请求报文");
    }

    @FXML
    void emptyAll() {
        courseTabTimeTextField.textProperty().setValue("");
        courseTabInputTextArea.textProperty().setValue("");
        courseTabMessageTextField.textProperty().setValue("请输入两个必填项");
    }
}
