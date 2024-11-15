package scau.os.soos.apps.taskManager.service;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import scau.os.soos.apps.taskManager.TaskManagerApp;
import scau.os.soos.apps.taskManager.TaskManagerService;
import scau.os.soos.common.OS;
import scau.os.soos.module.memory.MemoryController;
import scau.os.soos.module.memory.model.MemoryBlock;
import scau.os.soos.module.memory.view.MemoryReadView;
import scau.os.soos.module.process.model.PCB;
import scau.os.soos.module.process.model.Process;

import java.io.IOException;
import java.util.Random;

public class MemoryService implements TaskManagerService {
    private final ScrollPane detailContainer;
    private final VBox memoryDetail;
    private final AreaChart<String, Integer> overviewChart;
    private final AreaChart<String, Integer> usageChart;
    private final XYChart.Series<String, Integer> overviewSeries;
    private final XYChart.Series<String, Integer> detailSeries;
    private final HBox memoryBlockChart;
    private final Label overview;
    private final Label memoryUsage;
    private final Label memoryAvailable;
    private final Label memoryPCB;
    private MemoryReadView preData;

    public MemoryService(AreaChart<String, Integer> overviewChart, Label overview, ScrollPane detailContainer) {
        this.detailContainer = detailContainer;
        this.overviewChart = overviewChart;
        this.overview = overview;
        this.overviewSeries = new XYChart.Series<>();
        this.detailSeries = new XYChart.Series<>();
        // series不可重复绑定chart,因此用series绑定series
        detailSeries.setData(overviewSeries.getData());

        try {
            memoryDetail = FXMLLoader.load(TaskManagerApp.class.getResource("memory/memory.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.usageChart = (AreaChart<String, Integer>) memoryDetail.lookup("#memory-usage-chart");
        this.memoryUsage = (Label) memoryDetail.lookup("#memory-usage");
        this.memoryAvailable = (Label) memoryDetail.lookup("#memory-available");
        this.memoryPCB = (Label) memoryDetail.lookup("#memory-pcb");
        this.memoryBlockChart = (HBox) memoryDetail.lookup("#memory-block-chart");

        this.overviewChart.getData().add(overviewSeries);
        this.usageChart.getData().add(detailSeries);
    }

    @Override
    public void show() {
        detailContainer.setContent(memoryDetail);
    }

    @Override
    public void render() {
        MemoryReadView memoryData = MemoryController.getInstance().getData();
        boolean isBlockChange = checkBlockChange(memoryData);

        Platform.runLater(() -> {
            memoryUsage.setText(memoryData.usage() + "B");
            memoryAvailable.setText(memoryData.available() + "B");
            memoryPCB.setText(String.valueOf(memoryData.pcb()));
            if (isBlockChange) {
                memoryBlockChart.getChildren().clear();
                for (MemoryBlock block : memoryData.memoryBlockList()) {
                    memoryBlockChart.getChildren().add(newBlock(block.isFree(), (double) block.getSize() / memoryData.total()));
                }
            }
        });

        preData = memoryData;
    }


    @Override
    public void overview() {
        MemoryReadView memoryData = MemoryController.getInstance().getData();
        overviewSeries.getData().add(new XYChart.Data<>(String.valueOf(OS.clock.get()), memoryData.usage()));
        if (overviewSeries.getData().size() > 60) {
            overviewSeries.getData().removeFirst();
        }
        overview.setText(String.format("%d/%dB (%d%%)", memoryData.usage(), memoryData.total(), memoryData.usage() / memoryData.total()));
    }

    private Region newBlock(boolean isFree, double percent) {
        Region block = new Region();
        block.setPrefHeight(Region.USE_COMPUTED_SIZE);
        block.setPrefWidth(memoryBlockChart.getWidth() * percent);
        block.getStyleClass().add("memory-block");
        if (!isFree) {
            block.getStyleClass().add("active");
        }

        return block;
    }

    private boolean checkBlockChange(MemoryReadView memoryData) {
        boolean isBlockChange = false;

        // 检测内存块分布是否变化，减少渲染次数
        if (preData != null && memoryData.memoryBlockList().size() == preData.memoryBlockList().size()) {
            for (int i = 0; i < memoryData.memoryBlockList().size(); i++) {
                MemoryBlock block = memoryData.memoryBlockList().get(i);
                MemoryBlock preBlock = preData.memoryBlockList().get(i);
                if (block.isFree() != preBlock.isFree() && block.getSize() != preBlock.getSize()) {
                    isBlockChange = true;
                    break;
                }
            }
        } else {
            isBlockChange = true;
        }

        return isBlockChange;
    }
}
