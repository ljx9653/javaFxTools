package com.ljx.javaFx.utils;

import javafx.scene.control.Alert;

/**
 * @author lijx
 * @date 2021/1/18 - 18:34
 */
public class MessageUtil {

    /**
     * 用于javaFX 进行弹框提醒，弹出框只有消息标题，没有消息体
     *
     * @param title
     * @param message
     */
    public static void alert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
