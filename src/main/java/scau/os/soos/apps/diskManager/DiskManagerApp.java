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

    //private Pane occupationGraph;
    @FXML
    private StackPane occupationGraph;

    @FXML
    private TableView<DiskBlock> table; // 更新类型为 DiskBlock

    @FXML
    private TableColumn<DiskBlock, Integer> blockNumberColumn; // 盘块号列
    @FXML
    private TableColumn<DiskBlock, String> stateColumn; // 状态列


    private static final List<Label> labelList = new ArrayList();

    public class DiskBlock {
        private final Integer blockNumber;
        private final int state;

        public DiskBlock(Integer blockNumber, int state) {
            this.blockNumber = blockNumber;
            this.state = state;
        }

        public Integer getBlockNumber() {
            return blockNumber;
        }

        public int getState() {
            return state;
        }
    }


    public DiskManagerApp() {
        super("磁盘管理器", "main.fxml", 900, 560);
    }

    @Override
    protected void initialize() {


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

        occupationGraph.setStyle("-fx-border-color: lightgray; " +
                "-fx-border-width: 1; " +
                "-fx-border-style: solid; " +
                "-fx-padding: 10;");


        // 设置表格列的数据类型和工厂
        blockNumberColumn.setCellValueFactory(new PropertyValueFactory<>("blockNumber"));
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));

        // 初始化数据
        ObservableList<DiskBlock> diskBlocksData = FXCollections.observableArrayList();
        for (int i = 0; i < 256; i++) {
            diskBlocksData.add(new DiskBlock(i, i%100)); // 添加数据
        }

        // 设置数据到TableView
        table.setItems(diskBlocksData);

        // 创建饼图数据
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("空闲:"+30, 30),
                new PieChart.Data("占用:"+50, 50),
                new PieChart.Data("系统:"+20, 20)
        );

        // 创建饼图
        PieChart pieChart = new PieChart(pieChartData);


//        // 格式化饼图数据为百分比
//        pieChartData.forEach(data -> {
//            double percentage = (data.getPieValue() / pieChartData.stream().mapToDouble(PieChart.Data::getPieValue).sum()) * 100;
//            data.setName(String.format("%.1f%%", percentage));
//        });


        // 设置饼图的首选大小
        pieChart.setPrefSize(occupationGraph.getWidth(), occupationGraph.getHeight()); // 设置为200x200像素

        occupationGraph.getChildren().add(pieChart);

        occupationGraph.setClip(pieChart);





    }

    @Override
    protected void close() {

    }
}
