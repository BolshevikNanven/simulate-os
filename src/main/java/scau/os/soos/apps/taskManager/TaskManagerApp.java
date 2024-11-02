package scau.os.soos.apps.taskManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import scau.os.soos.apps.taskManager.service.CpuService;
import scau.os.soos.apps.taskManager.service.DeviceService;
import scau.os.soos.apps.taskManager.service.MemoryService;
import scau.os.soos.common.OS;
import scau.os.soos.common.enums.OS_STATES;
import scau.os.soos.common.model.Handler;
import scau.os.soos.ui.components.base.Window;

import java.util.Random;

public class TaskManagerApp extends Window {
    @FXML
    private ScrollPane detailContainer;
    @FXML
    private Label clock;
    @FXML
    private Button clockButton;
    @FXML
    private VBox cardContainer;
    @FXML
    private HBox cpuCard;
    @FXML
    private HBox memoryCard;
    @FXML
    private HBox deviceCard;
    @FXML
    private AreaChart<String, Integer> cpuOverviewChart;
    private XYChart.Series<String, Integer> cpuSeries;
    @FXML
    private AreaChart<String, Integer> memoryOverviewChart;
    private XYChart.Series<String, Integer> memorySeries;
    @FXML
    private AreaChart<String, Integer> deviceOverviewChart;
    private XYChart.Series<String, Integer> deviceSeries;
    private TaskManagerService cpuService;
    private TaskManagerService memoryService;
    private TaskManagerService deviceService;
    private Handler handler;
    private String activeCardName;

    public TaskManagerApp() {
        super("任务管理器", "main.fxml", 920, 580);
    }

    @Override
    protected void initialize() {
        if (OS.state.equals(OS_STATES.RUNNING)) {
            clockButton.setText("暂停时钟");
        } else {
            clockButton.setText("开启时钟");
        }

        handler = () -> {
            switch (activeCardName) {
                case "cpu" -> cpuService.render();
                case "memory" -> memoryService.render();
                case "device" -> deviceService.render();
            }
            Platform.runLater(() -> {
                clock.setText(String.valueOf(OS.clock.get()));
                renderOverview();
            });

        };

        cpuService = new CpuService(detailContainer);
        memoryService = new MemoryService(detailContainer);
        deviceService = new DeviceService(detailContainer);

        cpuSeries = new XYChart.Series<>();
        memorySeries = new XYChart.Series<>();
        deviceSeries = new XYChart.Series<>();

        cpuOverviewChart.getData().add(cpuSeries);
        memoryOverviewChart.getData().add(memorySeries);
        deviceOverviewChart.getData().add(deviceSeries);

        addListener();

        OS.clock.bind(handler);
        setActiveCard("cpu");
    }

    @Override
    protected void close() {
        OS.clock.unBind(handler);
    }

    private void addListener() {
        cpuCard.setOnMouseClicked(mouseEvent -> setActiveCard("cpu"));
        memoryCard.setOnMouseClicked(mouseEvent -> setActiveCard("memory"));
        deviceCard.setOnMouseClicked(mouseEvent -> setActiveCard("device"));
        clockButton.setOnAction(actionEvent -> {
            if (OS.state.equals(OS_STATES.RUNNING)) {
                OS.state = OS_STATES.PAUSE;
                clockButton.setText("开启时钟");
            } else {
                OS.state = OS_STATES.RUNNING;
                clockButton.setText("暂停时钟");
            }
        });
    }

    private void setActiveCard(String card) {
        for (Node child : cardContainer.getChildren()) {
            child.getStyleClass().remove("active");
        }
        switch (card) {
            case "cpu" -> {
                cpuCard.getStyleClass().add("active");
                cpuService.show();
            }
            case "memory" -> {
                memoryCard.getStyleClass().add("active");
                memoryService.show();
            }
            case "device" -> {
                deviceCard.getStyleClass().add("active");
                deviceService.show();
            }
        }
        activeCardName = card;
    }

    private void renderOverview() {
        cpuSeries.getData().add(new XYChart.Data<>(String.valueOf(OS.clock.get()), new Random().nextInt(5)));
        if (cpuSeries.getData().size() > 20) {
            cpuSeries.getData().removeFirst();
        }

        memorySeries.getData().add(new XYChart.Data<>(String.valueOf(OS.clock.get()), new Random().nextInt(5)));
        if (memorySeries.getData().size() > 20) {
            memorySeries.getData().removeFirst();
        }

        deviceSeries.getData().add(new XYChart.Data<>(String.valueOf(OS.clock.get()), new Random().nextInt(5)));
        if (deviceSeries.getData().size() > 20) {
            deviceSeries.getData().removeFirst();
        }
    }
}
