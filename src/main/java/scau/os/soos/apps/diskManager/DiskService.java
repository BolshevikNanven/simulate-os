package scau.os.soos.apps.diskManager;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.model.Fat;
import scau.os.soos.module.file.model.Item;

import java.util.ArrayList;
import java.util.List;


public class DiskService {

    private final Fat fatTable;

    private final List<Item> rootDir;

    public DiskService() {
        fatTable = FileController.getInstance().getFat();

        rootDir = new ArrayList<>();
        rootDir.add(FileController.getInstance().getPartitionDirectory());
        rootDir.addAll(FileController.getInstance().listRoot());

    }



    public class DiskBlock {
        private final IntegerProperty blockNumber = new SimpleIntegerProperty(this, "blockNumber");
        private final StringProperty state = new SimpleStringProperty(this, "state");

        public DiskBlock(Integer blockNumber, String state) {
            this.blockNumber.set(blockNumber);
            this.state.set(state);
        }

        public IntegerProperty blockNumberProperty() {
            return blockNumber;
        }

        public int getBlockNumber() {
            return blockNumber.get();
        }

        public StringProperty stateProperty() {
            return state;
        }

        public String getState() {
            return state.get();
        }
    }

    public void diskRender(GridPane diskBlocks) {
        diskBlocks.setHgap(3); // 水平间隔
        diskBlocks.setVgap(1.5); // 垂直间隔


        for (int i = 0; i < 256; i++) {
            Label diskBlock = new Label("" + (i));
            diskBlock.setPrefWidth(30); // 设置Label的首选宽度
            diskBlock.setAlignment(Pos.CENTER);
            diskBlock.setStyle("-fx-background-color: #A9A9A9; " + // 深灰色
                    "-fx-border-style: solid; " +
                    "-fx-border-color: #808080; " +
                    "-fx-border-width: 1;");

//             根据fat表的返回值设置背景颜色
            for (Item item : rootDir) {
                if (!fatTable.isFreeBlock(i, item.getStartBlockNum())) {
                    diskBlock.setTextFill(Color.WHITE); // 设置文本颜色为白色，以提高对比度
                    diskBlock.setStyle("-fx-background-color: " + toWebColor(Color.FIREBRICK) + "; " +
                            "-fx-border-style: solid; " +
                            "-fx-border-color: #808080; " +
                            "-fx-border-width: 1;");
                }
            }
            diskBlocks.add(diskBlock, i % 16, i / 16); // 将Label添加到GridPane
        }
    }

    public void tableRender(TableColumn<DiskService.DiskBlock, Integer> blockNumberColumn, TableColumn<DiskService.DiskBlock, String> stateColumn, StackPane occupationGraph, TableView<DiskBlock> table) {
        occupationGraph.setStyle("-fx-border-color: lightgray; " +
                "-fx-border-width: 1; " +
                "-fx-border-style: solid; " +
                "-fx-padding: 10;");

        // 设置表格列的数据类型和工厂
        blockNumberColumn.setCellValueFactory(new PropertyValueFactory<>("blockNumber"));
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));

        // 设置stateColumn的单元格工厂
        stateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle(getStateStyleBasedOnValue(item));
                }
            }
        });

        // 初始化数据
        ObservableList<DiskBlock> diskBlocksData = FXCollections.observableArrayList();
        for (int i = 0; i < 256; i++) {
            DiskBlock diskBlock = null;

            if (i < 4) {
                diskBlock = new DiskBlock(i, "系统占用");
            } else {
                for (Item item : rootDir) {
                    diskBlock = new DiskBlock(i, fatTable.isFreeBlock(i, item.getStartBlockNum()) ? "空闲" : "占用");
                }
            }

            // 添加监听器
            diskBlock.blockNumberProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println("Block number changed from " + oldValue + " to " + newValue);
            });

            diskBlock.stateProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println("State changed from " + oldValue + " to " + newValue);
            });
            diskBlocksData.add(diskBlock);
        }

        // 设置数据到TableView
        table.setItems(diskBlocksData);
    }

    public void occupationGraphRender(StackPane occupationGraph) {
        // 获取空闲和占用的块的数量
        int totalBlocks = 256; // 假设总共有256个块
        int freeBlocks = 0;
        for (int i = 0; i < totalBlocks; i++) {
            for (Item item : rootDir) {
                if (fatTable.isFreeBlock(i,item.getStartBlockNum())) {
                    freeBlocks++;
                }
            }
        }
        int occupiedBlocks = totalBlocks - freeBlocks;

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("空闲:" + freeBlocks, freeBlocks),
                new PieChart.Data("占用:" + occupiedBlocks, occupiedBlocks)
        );

        // 创建饼图
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("磁盘使用情况");

        // 启用动画
        pieChart.setAnimated(true);

        // 设置图例
        pieChart.setLegendVisible(true);

        // 自定义样式

        // 清除之前的饼图（如果有）并添加新的饼图到StackPane
        occupationGraph.getChildren().clear();
        occupationGraph.getChildren().add(pieChart);
    }

    private String toWebColor(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private String getStateStyleBasedOnValue(String state) {
        switch (state) {
            case "系统占用":
                return "-fx-text-fill: gray;";
            case "空闲":
                return "-fx-text-fill: green;";
            case "占用":
                return "-fx-text-fill: red;";
            default:
                return "";
        }
    }
}
