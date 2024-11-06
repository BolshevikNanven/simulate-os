package scau.os.soos.apps.taskManager.service;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import scau.os.soos.apps.taskManager.TaskManagerApp;
import scau.os.soos.apps.taskManager.TaskManagerService;
import scau.os.soos.common.OS;
import scau.os.soos.module.cpu.CpuController;
import scau.os.soos.module.cpu.view.CpuReadView;
import scau.os.soos.module.process.ProcessController;
import scau.os.soos.module.process.view.ProcessOverviewReadView;

import java.io.IOException;

public class CpuService implements TaskManagerService {
    private final ScrollPane detailContainer;
    private final VBox cpuDetail;
    private final AreaChart<String, Integer> overviewChart;
    private final Label overview;
    private final AreaChart<String, Integer> processChart;
    private final XYChart.Series<String, Integer> overviewSeries;
    private final XYChart.Series<String, Integer> processSeries;
    private final Label cpuState;
    private final Label cpuTimeSlice;
    private final Label cpuPid;
    private final Label cpuInstruction;
    private final Label cpuAX;

    public CpuService(AreaChart<String, Integer> overviewChart, Label overview, ScrollPane detailContainer) {
        this.detailContainer = detailContainer;
        this.overviewChart = overviewChart;
        this.overview = overview;
        this.processSeries = new XYChart.Series<>();
        this.overviewSeries = new XYChart.Series<>();
        this.processSeries.setData(overviewSeries.getData());

        try {
            cpuDetail = FXMLLoader.load(TaskManagerApp.class.getResource("cpu/cpu.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        processChart = (AreaChart<String, Integer>) cpuDetail.lookup("#process-chart");
        cpuState = (Label) cpuDetail.lookup("#cpu-state");
        cpuTimeSlice = (Label) cpuDetail.lookup("#cpu-time-slice");
        cpuPid = (Label) cpuDetail.lookup("#cpu-pid");
        cpuInstruction = (Label) cpuDetail.lookup("#cpu-instruction");
        cpuAX = (Label) cpuDetail.lookup("#cpu-AX");

        this.overviewChart.getData().add(overviewSeries);
        this.processChart.getData().add(processSeries);
    }

    @Override
    public void show() {
        detailContainer.setContent(cpuDetail);
    }

    @Override
    public void render() {
        CpuReadView cpuReadView = CpuController.getInstance().getData();

        Platform.runLater(() -> {
            cpuPid.setText(String.valueOf(cpuReadView.pid()));
            cpuInstruction.setText(cpuReadView.instruction());
            cpuAX.setText(String.valueOf(cpuReadView.AX()));
        });

    }

    @Override
    public void overview() {
        ProcessOverviewReadView overviewReadView = ProcessController.getInstance().getOverview();
        overviewSeries.getData().add(new XYChart.Data<>(String.valueOf(OS.clock.get()), overviewReadView.total()));
        if (overviewSeries.getData().size() > 60) {
            overviewSeries.getData().removeFirst();
        }
        overview.setText(String.format("%s %d个进程", overviewReadView.busy() ? "忙碌" : "空闲", overviewReadView.total()));
        cpuState.setText(overviewReadView.busy() ? "忙碌中" : "空闲");
        cpuTimeSlice.setText(String.valueOf(overviewReadView.timeSlice()));
    }
}
