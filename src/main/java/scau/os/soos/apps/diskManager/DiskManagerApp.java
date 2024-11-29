package scau.os.soos.apps.diskManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;

import scau.os.soos.apps.diskManager.model.DiskBlock;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.Notifier;
import scau.os.soos.module.file.model.Item;
import scau.os.soos.ui.components.base.Window;

import java.util.ArrayList;
import java.util.List;

public class DiskManagerApp extends Window implements Notifier {
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
    private TableView<DiskBlock> table; // 更新类型为 DiskBlock
    @FXML
    private TableColumn<DiskBlock, Integer> blockNumberColumn; // 盘块号列
    @FXML
    private TableColumn<DiskBlock, String> stateColumn; // 状态列
    @FXML
    private TableColumn<DiskBlock, Integer> indexColumn; // 索引列

    private static final List<Label> labelList = new ArrayList<>();

    private DiskService service;

    public DiskManagerApp() {
        super("磁盘管理器", "main.fxml", 900, 560);
    }

    @Override
    protected void initialize() {
        service = new DiskService();

        refreshDiskData(service);

        FileController.getInstance().bind(this);
    }

    private void refreshDiskData(DiskService service) {
        // 执行刷新操作
        service.refreshRootDir();
        service.diskRender(diskBlocks);
        service.tableRender(blockNumberColumn, stateColumn, indexColumn,occupationGraph, table);
        service.occupationGraphRender(occupationGraph);
    }

    @Override
    protected void close() {

    }

    @Override
    public void update(Item item) {
        refreshDiskData(service);
    }
}