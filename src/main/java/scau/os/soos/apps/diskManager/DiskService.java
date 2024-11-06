package scau.os.soos.apps.diskManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.model.Fat;



public class DiskService {

    Fat fatTable = FileController.getInstance().getFat();

    public class DiskBlock {
        private final Integer blockNumber;
        private final String state;

        Fat fatTable = FileController.getInstance().getFat();

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

    public  void diskRender(GridPane diskBlocks) {
        diskBlocks.setHgap(3); // 水平间隔
        diskBlocks.setVgap(1.5); // 垂直间隔
        Color[] occupiedColors = {Color.DARKRED, Color.SALMON, Color.ORANGERED, Color.CRIMSON, Color.FIREBRICK}; // 占用盘块的颜色数组

        for (int i = 0; i < 256; i++) {
            Label diskBlock = new Label("" + (i));
            diskBlock.setPrefWidth(30); // 设置Label的首选宽度
            diskBlock.setAlignment(Pos.CENTER);
            diskBlock.setStyle("-fx-background-color: #A9A9A9; " + // 深灰色
                    "-fx-border-style: solid; " +
                    "-fx-border-color: gray; " +
                    "-fx-border-width: 1;");

            // 根据fat表的返回值设置背景颜色
            if (!fatTable.isFreeBlock(i)) {
                // 为占用的盘块分配不同的颜色
                int colorIndex = i % occupiedColors.length;
                diskBlock.setTextFill(Color.WHITE); // 设置文本颜色为白色，以提高对比度
                diskBlock.setStyle("-fx-background-color: " + toWebColor(occupiedColors[colorIndex]) + "; "+
                        "-fx-border-style: solid; " +
                        "-fx-border-color: gray; " +
                        "-fx-border-width: 1;");
            }

            diskBlocks.add(diskBlock, i % 16, i / 16); // 将Label添加到GridPane
        }
    }

    public void tableRender(TableColumn<DiskService.DiskBlock, Integer> blockNumberColumn,TableColumn<DiskService.DiskBlock, String> stateColumn,StackPane occupationGraph, TableView<DiskBlock> table) {
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
            diskBlocksData.add(new DiskBlock(i, fatTable.isFreeBlock(i) ? "空闲" : "占用")); // 添加数据
        }

        // 设置数据到TableView
        table.setItems(diskBlocksData);
    }

    public void occupationGraphRender(StackPane occupationGraph) {
        // 创建饼图数据
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("空闲:" + 30, 30),
                new PieChart.Data("占用:" + 50, 50),
                new PieChart.Data("系统:" + 20, 20)
        );

        // 创建饼图
        PieChart pieChart = new PieChart(pieChartData);

        // 设置饼图的首选大小
        pieChart.setPrefSize(occupationGraph.getWidth(), occupationGraph.getHeight()); // 设置为200x200像素

        occupationGraph.getChildren().add(pieChart);

        occupationGraph.setClip(pieChart);
    }


    private String toWebColor(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

}
