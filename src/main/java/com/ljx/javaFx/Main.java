package com.ljx.javaFx;

/**
 * @author lijx
 * @date 2019/4/11 - 9:45
 */

import com.ljx.javaFx.controller.ExcelController;
import com.ljx.javaFx.controller.TimeStatisticsController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            //如果写ClassLoader就不写/，不写ClassLoader就要加/
//Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("tools.fxml"));
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/tools.fxml"));
            primaryStage.setTitle("tools");
            primaryStage.setScene(new Scene(root));
            //此条语句上传文件需要用到
            TimeStatisticsController.setStage(primaryStage);
            ExcelController.setStage(primaryStage);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
