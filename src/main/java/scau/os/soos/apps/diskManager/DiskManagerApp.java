package scau.os.soos.apps.diskManager;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
    private Pane occupationGraph;

    @FXML
    private TableView<DiskBlock> table; // 更新类型为 DiskBlock

    @FXML
    private TableColumn<DiskBlock, Integer> blockNumberColumn; // 盘块号列
    @FXML
    private TableColumn<DiskBlock, String> stateColumn; // 状态列


    private static final List<Label> labelList = new ArrayList();

    public class DiskBlock {
        private final Integer blockNumber;
        private final String state;

        public DiskBlock(Integer blockNumber, String state) {
            this.blockNumber = blockNumber;
            this.state = state;
        }

        public Integer getBlockNumber() {
            return blockNumber;
        }

        public String getState() {
            return state;
        }
    }


    public DiskManagerApp() {
        super("磁盘管理器", "main.fxml", 900, 560);
    }

    @Override
    protected void initialize() {


//        Rectangle rect = new Rectangle(10, 25, 40, 20);
//        rect.setArcWidth(10);
//        rect.setArcHeight(10);
//        rect.setFill(Color.BLUE);
//
//        diskBlocks.getChildren().add(rect);
//
//        diskBlocks = new GridPane();
        diskBlocks.setHgap(3); // 水平间隔
        diskBlocks.setVgap(1.5); // 垂直间隔
        for (int i = 0; i < 256; i++) {
            Label diskBlock = new Label( ""+(i));
            diskBlock.setTextFill(Color.LIGHTGRAY); // 设置默认颜色
            diskBlock.setPrefWidth(30); // 设置Label的首选宽度
            diskBlock.setAlignment(Pos.CENTER);
            diskBlock.setStyle("-fx-background-color: #A9A9A9; " + // 深灰色
                    "-fx-border-style: solid; " +
                    "-fx-border-width: 3;");
            diskBlocks.add(diskBlock, i % 16, i / 16); // 将Rectangle添加到GridPane
        }


        // 设置表格列的数据类型和工厂
        blockNumberColumn.setCellValueFactory(new PropertyValueFactory<>("blockNumber"));
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));

        // 初始化数据
        ObservableList<DiskBlock> diskBlocksData = FXCollections.observableArrayList();
        for (int i = 0; i < 256; i++) {
            diskBlocksData.add(new DiskBlock(i, "Free")); // 添加数据
        }

        // 设置数据到TableView
        table.setItems(diskBlocksData);
    }

    @Override
    protected void close() {

    }
}
