package scau.os.soos.apps.diskManager;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import scau.os.soos.ui.components.base.Window;

import java.util.ArrayList;
import java.util.List;

public class DiskManagerApp extends Window {
    @FXML
    private BorderPane body;
    @FXML
    private GridPane diskBlocks;

    @FXML
    private HBox states;
    @FXML
    private Label title;
    @FXML
    private BorderPane detailDisplay;
    @FXML
    private ScrollPane occupation;

    @FXML
    private StackPane occupationGraph;
    @FXML
    private Label ReFresh;

    @FXML
    private TableView<DiskService.DiskBlock> table; // 更新类型为 DiskBlock

    @FXML
    private TableColumn<DiskService.DiskBlock, Integer> blockNumberColumn; // 盘块号列
    @FXML
    private TableColumn<DiskService.DiskBlock, String> stateColumn; // 状态列


    private static final List<Label> labelList = new ArrayList<>();


    public DiskManagerApp() {
        super("磁盘管理器", "main.fxml", 900, 560);
    }

    @Override
    protected void initialize() {

        DiskService service = new DiskService();
        refreshDiskData(service);

        // 为 Refresh 按钮添加点击动画
        addRefreshAnimation();

        // 为 ReFresh 按钮的点击事件添加刷新逻辑
        ReFresh.setOnMouseClicked(event -> refreshDiskData(service));

        // 监听点击子界面以获取焦点
        this.body.setOnMouseClicked(event -> {
            // 当点击子界面时，获取焦点
            body.requestFocus();
        });

        // 添加键盘事件监听器，监听 F 键
        body.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("F")) {
                refreshDiskData(service);
            }
        });
    }

    private void addRefreshAnimation() {
        // 创建一个缩放动画（点击时缩放一下）
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), ReFresh);
        scaleTransition.setByX(0.1);  // X轴缩放 10%
        scaleTransition.setByY(0.1);  // Y轴缩放 10%
        scaleTransition.setCycleCount(2);  // 动画执行两次（放大和恢复）
        scaleTransition.setAutoReverse(true);  // 动画结束时返回原位

        // 创建颜色变化动画
        ReFresh.setOnMousePressed(event -> {
            // 按下时变色
            ReFresh.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
        });

        ReFresh.setOnMouseReleased(event -> {
            // 松开时恢复原色
            ReFresh.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        });
        // 添加点击事件监听器
        ReFresh.setOnMouseClicked(event -> {
            // 点击时执行缩放动画
            scaleTransition.play();
        });
    }

    private void refreshDiskData(DiskService service) {
        // 执行刷新操作
        service.diskRender(diskBlocks);
        service.tableRender(blockNumberColumn, stateColumn, occupationGraph, table);
        service.occupationGraphRender(occupationGraph);
    }

    @Override
    protected void close() {
    }


}