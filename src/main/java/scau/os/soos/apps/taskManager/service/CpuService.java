package scau.os.soos.apps.taskManager.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import scau.os.soos.apps.taskManager.TaskManagerApp;
import scau.os.soos.apps.taskManager.TaskManagerService;

import java.io.IOException;

public class CpuService implements TaskManagerService {
    private final ScrollPane detailContainer;
    private final VBox cpuDetail;
    private final AreaChart<String, Integer> overviewChart;
    private final Label overview;
    public CpuService(AreaChart<String, Integer> overviewChart, Label overview, ScrollPane detailContainer) {
        this.detailContainer = detailContainer;
        this.overviewChart = overviewChart;
        this.overview = overview;
        try {
            cpuDetail = FXMLLoader.load(TaskManagerApp.class.getResource("cpu/cpu.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void show() {
        detailContainer.setContent(cpuDetail);
    }

    @Override
    public void render() {

    }

    @Override
    public void overview() {

    }
}
