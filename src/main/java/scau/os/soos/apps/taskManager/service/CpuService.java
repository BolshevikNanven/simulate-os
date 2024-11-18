package scau.os.soos.apps.taskManager.service;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import scau.os.soos.apps.taskManager.TaskManagerApp;
import scau.os.soos.apps.taskManager.TaskManagerService;
import scau.os.soos.common.OS;
import scau.os.soos.module.cpu.CpuController;
import scau.os.soos.module.cpu.view.CpuReadView;
import scau.os.soos.module.process.ProcessController;
import scau.os.soos.module.process.view.ProcessOverviewReadView;
import scau.os.soos.module.process.view.ProcessReadView;

import java.io.IOException;
import java.util.List;

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
    private final VBox runningProcessList;
    private final VBox readyProcessList;
    private final VBox blockProcessList;

    public CpuService(AreaChart<String, Integer> overviewChart, Label overview, ScrollPane detailContainer) {
        this.detailContainer = detailContainer;
        this.overviewChart = overviewChart;
        this.overview = overview;
        this.processSeries = new XYChart.Series<>();
        this.overviewSeries = new XYChart.Series<>();
        processSeries.setData(overviewSeries.getData());

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

        runningProcessList = (VBox) cpuDetail.lookup("#running-process-list");
        readyProcessList = (VBox) cpuDetail.lookup("#ready-process-list");
        blockProcessList = (VBox) cpuDetail.lookup("#block-process-list");

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
        List<ProcessReadView> processReadViews = ProcessController.getInstance().getData();

        Platform.runLater(() -> {
            cpuPid.setText(String.valueOf(cpuReadView.pid()));
            cpuInstruction.setText(cpuReadView.instruction());
            cpuAX.setText(String.valueOf(cpuReadView.AX()));
            runningProcessList.getChildren().clear();
            readyProcessList.getChildren().clear();
            blockProcessList.getChildren().clear();
            for (ProcessReadView process : processReadViews) {
                switch (process.state()) {
                    case RUNNING ->
                            runningProcessList.getChildren().add(newProcessItem(process.pid(), "运行", process.memory()));
                    case READY ->
                            readyProcessList.getChildren().add(newProcessItem(process.pid(), "就绪", process.memory()));
                    case BLOCKED ->
                            blockProcessList.getChildren().add(newProcessItem(process.pid(), "阻塞", process.memory()));
                }
            }
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

    private HBox newProcessItem(int pid, String state, int memory) {
        try {
            HBox item = FXMLLoader.load(TaskManagerApp.class.getResource("cpu/process_item.fxml"));
            Label pidLabel = (Label) item.lookup("#process-item-pid");
            Label stateLabel = (Label) item.lookup("#process-item-state");
            Label memoryLabel = (Label) item.lookup("#process-item-memory");

            pidLabel.setText(String.valueOf(pid));
            stateLabel.setText(state);
            memoryLabel.setText(memory + "B");

            return item;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
