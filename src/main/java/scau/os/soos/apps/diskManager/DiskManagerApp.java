package scau.os.soos.apps.diskManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.model.Fat;
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
        service.diskRender(diskBlocks);
        service.tableRender(blockNumberColumn, stateColumn, occupationGraph, table);
        service.occupationGraphRender(occupationGraph);

    }

    @Override
    protected void close() {
    }


}