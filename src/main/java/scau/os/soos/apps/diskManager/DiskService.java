package scau.os.soos.apps.diskManager;

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

import scau.os.soos.apps.diskManager.model.DiskBlock;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.FileService;
import scau.os.soos.module.file.model.Directory;
import scau.os.soos.module.file.model.Fat;
import scau.os.soos.module.file.model.Item;

import java.util.*;


public class DiskService {

    private final Fat fatTable;

    private final List<Item> rootDir;

    private final Map<Item, String> itemColorMap; // 存储每个Item的颜色

    // 定义一个颜色列表，用于为Item分配颜色
    private static final List<String> COLORS = Arrays.asList(
            "#f0f8ff", // AliceBlue
            "#ffebcd", // PaleGoldenRod
            "#afffff", // PaleTurquoise
            "#98fb98", // PaleGreen
            "#db7093", // PaleVioletRed
            "#ffe4e1", // MistyRose
            "#fadead", // LightSalmon
            "#add8e6"  // LightBlue
            // 可以添加更多颜色...
    );

    public DiskService() {
        fatTable = FileController.getInstance().getFat();

        rootDir = new ArrayList<>();
        refreshRootDir();

        itemColorMap = new HashMap<>();
        assignColorsToItems();
    }

    public void refreshRootDir() {
        rootDir.clear();
        rootDir.add(FileController.getInstance().getPartitionDirectory());
        rootDir.addAll(FileController.getInstance().listRoot());
    }

    // 为每个Item分配颜色
    private void assignColorsToItems() {
        Iterator<String> colorIterator = COLORS.iterator();
        for (Item item : rootDir) {
            if (colorIterator.hasNext()) {
                String color = colorIterator.next();
                itemColorMap.put(item, "-fx-background-color: " + color + ";-fx-border-style: solid; -fx-border-color: #212121; -fx-border-width: 1;");
            } else {
                // 如果没有足够的颜色，可以重复使用颜色或添加更多颜色到COLORS列表中
                // 这里我们简单地重复使用颜色
                colorIterator = COLORS.iterator(); // 重置迭代器
                String color = colorIterator.next();
                itemColorMap.put(item, "-fx-background-color: " + color + ";-fx-border-style: solid; -fx-border-color: #212121; -fx-border-width: 1;");
            }
        }
    }


    public void diskRender(GridPane diskBlocks) {
        diskBlocks.setHgap(5);
        diskBlocks.setVgap(5);

        for (int i = 0; i < 256; i++) {
            Label diskBlock = new Label("" + (i));
            diskBlock.setPrefWidth(50);
            diskBlock.setPrefHeight(50);
            diskBlock.setAlignment(Pos.CENTER);
            diskBlock.setTextFill(Color.BLACK);
            diskBlock.setStyle("-fx-background-color:#008000;-fx-border-style: solid; -fx-border-color: #212121; -fx-border-width: 1;");

            for (Item item : rootDir) {
                if (fatTable.isFreeBlock(i, item.getStartBlockNum())) {
                    diskBlock.setStyle(itemColorMap.get(item));
                    break;
                }
            }

            diskBlocks.add(diskBlock, i % 16, i / 16);
        }
    }

    public void tableRender(TableColumn<DiskBlock, Integer> blockNumberColumn, TableColumn<DiskBlock, String> stateColumn, TableColumn<DiskBlock, Integer> indexColumn, StackPane occupationGraph, TableView<DiskBlock> table) {
        occupationGraph.setStyle("-fx-border-color: lightgray; " +
                "-fx-border-width: 1; " +
                "-fx-border-style: solid; " +
                "-fx-padding: 10;");

        // 设置表格列的数据类型和工厂
        blockNumberColumn.setCellValueFactory(new PropertyValueFactory<>("blockNumber"));
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        indexColumn.setCellValueFactory(new PropertyValueFactory<>("nextIndex"));

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
        List<Integer> rootBlockStartNum = new ArrayList<>();
        for(Item item : rootDir){
            rootBlockStartNum.add(item.getStartBlockNum());
        }
        for (int i = 0; i < 256; i++) {
            DiskBlock diskBlock = null;

            if (i < 5) {
                diskBlock = new DiskBlock(i, "系统占用",fatTable.getNextBlockIndex(i));
            } else {
                String statue = null;
                if(rootBlockStartNum.contains(fatTable.getNextBlockIndex(i))) statue = "空闲";
                else statue = "占用";
                diskBlock = new DiskBlock(i, statue,fatTable.getNextBlockIndex(i));
            }

            diskBlocksData.add(diskBlock);
        }

        // 设置数据到TableView
        table.setItems(diskBlocksData);
    }

    public void occupationGraphRender(StackPane occupationGraph) {

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for(Item item : rootDir){
            if(item.getStartBlockNum()<5) continue;
            int occupyBlocks = FileService.getDirectoryTotalDiskBlocks((Directory) item);
            int freeBlocks = item.getSize() - occupyBlocks;
            // 只添加占用部分和空闲部分都不为零的情况
            if (occupyBlocks > 0) {
                pieChartData.add(new PieChart.Data(item.getName() + "占:" + occupyBlocks, occupyBlocks));
            }
            if (freeBlocks > 0) {
                pieChartData.add(new PieChart.Data(item.getName() + "空:" + freeBlocks, freeBlocks));
            }
        }

        // 创建饼图
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("磁盘使用情况");

        // 启用动画
        pieChart.setAnimated(true);
        pieChart.setLegendVisible(false);

        // 清除之前的饼图（如果有）并添加新的饼图到StackPane
        occupationGraph.getChildren().clear();
        occupationGraph.getChildren().add(pieChart);
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
