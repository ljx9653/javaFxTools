package com.ljx.javaFx.simple;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * @author lijx
 * @date 2019/4/10 - 10:52
 */
public class FxApplication extends Application {
    // 在此定义按钮可以让多个方法进行访问
    private final Button btnApply = new Button("应用");
    private final Button btnContinue = new Button("继续");
    private final Button btnExit = new Button("退出");

    public static void main(String[] args) {
        Application.launch(FxApplication.class, args);
    }

    @Override
    public void start(Stage primaryStage) {

        // 使用较大的标签字体使“退出”按钮更大
//        btnExit.setStyle("-fx-font-size: 15pt;");

        // 使用选项卡窗格，其中一个选项卡用于调整UI，一个选项卡用于对齐UI
        TabPane tabs = new TabPane();
        Tab tabSize = new Tab();
        tabSize.setText("调整大小");
        tabSize.setContent(sizingSample());

        Tab tabAlign = new Tab();
        tabAlign.setText("对齐");
        tabAlign.setContent(alignmentSample());

        tabs.getTabs().addAll(tabSize, tabAlign);

        Scene scene = new Scene(tabs, 600, 500); // 管理场景大小
        primaryStage.setTitle("工具箱");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * 创建大小调整示例的tab，演示在不需要默认大小时管理控件大小的方法.
     */
    private Pane sizingSample() {

        BorderPane border = new BorderPane();
        border.setPadding(new Insets(20, 0, 20, 20));

        ListView<String> lvList = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList (
                "热狗", "汉堡包", "炸薯条",
                "胡萝卜条", "鸡肉沙拉");
        lvList.setItems(items);
        lvList.setMaxHeight(Control.USE_PREF_SIZE);
        lvList.setPrefWidth(150.0);

        border.setLeft(lvList);
        border.setRight(createButtonColumn());
        border.setBottom(createButtonRow());  // 使用平铺窗格进行大小调整
//        border.setBottom(createButtonBox());  // 使用HBox，没有尺寸

        return border;
    }

    /**
     * 创建对齐示例的tab，演示在不需要默认对齐时管理控件对齐的方法。
     */
    private Pane alignmentSample() {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);  //覆盖默认值
        grid.setHgap(10);
        grid.setVgap(12);

        // Use column constraints to set properties for columns in the grid
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHalignment(HPos.RIGHT);  // 覆盖默认值
        grid.getColumnConstraints().add(column1);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHalignment(HPos.LEFT);  // 覆盖默认值
        grid.getColumnConstraints().add(column2);

        HBox hbButtons = new HBox();
        hbButtons.setSpacing(10.0);
        hbButtons.setAlignment(Pos.CENTER);  // 将HBox与HBox中的对照物对齐


        Button btnSubmit = new Button("提交");
        Button btnClear = new Button("清空");
        Button btnExit2 = new Button("退出");
        //使按钮更大
//        btnSubmit.setStyle("-fx-font-size: 15pt;");

        Label lblName = new Label("用户名：");
        TextField tfName = new TextField();
        Label lblPwd = new Label("密码：");
        PasswordField pfPwd = new PasswordField();

        hbButtons.getChildren().addAll(btnSubmit, btnClear, btnExit2);
        grid.add(lblName, 0, 0);
        grid.add(tfName, 1, 0);
        grid.add(lblPwd, 0, 1);
        grid.add(pfPwd, 1, 1);
        grid.add(hbButtons, 0, 2, 2, 1);

        /* 取消注释以下语句以对齐按钮 */
        hbButtons.setAlignment(Pos.BOTTOM_CENTER);
        GridPane innergrid = new GridPane();
        innergrid.setAlignment(Pos.CENTER);
        innergrid.add(hbButtons, 0, 0);
        grid.add(innergrid, 0, 2, 2, 1);

        return grid;
    }

    /**
     * 创建一列按钮，使它们的宽度与最大按钮的宽度相同。
     */
    private VBox createButtonColumn() {

        Button btnAdd = new Button("添加");
        Button btnDelete = new Button("删除");
        Button btnMoveUp = new Button("上移");
        Button btnMoveDown = new Button("下移");

        // Comment out the following statements to see the default button sizes
        btnAdd.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnDelete.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnMoveUp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnMoveDown.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnMoveDown.setMinWidth(Control.USE_PREF_SIZE);

        VBox vbButtons = new VBox();
        vbButtons.setSpacing(10);
        vbButtons.setPadding(new Insets(0, 20, 10, 20));

        vbButtons.getChildren().addAll(
                btnAdd, btnDelete, btnMoveUp, btnMoveDown);

        return vbButtons;
    }

    /**
     * 创建一行按钮，使它们的大小相同。
     */
    private TilePane createButtonRow() {

        // Let buttons grow, otherwise they will be different sizes based
        // on the length of the label
        btnApply.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnContinue.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btnExit.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        TilePane tileButtons = new TilePane(Orientation.HORIZONTAL);
        tileButtons.setPadding(new Insets(20, 10, 20, 0));
        tileButtons.setHgap(10.0);
        tileButtons.setVgap(8.0); // 以防窗口和按钮缩小
        // 需要另一排
        tileButtons.getChildren().addAll(btnApply, btnContinue, btnExit);

        return tileButtons;
    }

    /**
     * 使用默认大小创建一行按钮。
     */
    private HBox createButtonBox() {

        HBox hbButtons = new HBox();
        hbButtons.setSpacing(10);
        hbButtons.setPadding(new Insets(20, 10, 20, 0));
        hbButtons.getChildren().addAll(btnApply, btnContinue, btnExit);

        return hbButtons;
    }
}
