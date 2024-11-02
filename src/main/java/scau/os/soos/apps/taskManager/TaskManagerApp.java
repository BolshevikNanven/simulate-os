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

import java.util.HashMap;
import java.util.Map;
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
    @FXML
    private Label cpuOverview;
    @FXML
    private AreaChart<String, Integer> memoryOverviewChart;
    @FXML
    private Label memoryOverview;
    @FXML
    private AreaChart<String, Integer> deviceOverviewChart;
    @FXML
    private Label deviceOverview;
    private Map<String, TaskManagerService> serviceMap;
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
            serviceMap.get(activeCardName).render();
            Platform.runLater(() -> {
                clock.setText(String.valueOf(OS.clock.get()));
                for (TaskManagerService service : serviceMap.values()) {
                    service.overview();
                }
            });

        };

        serviceMap = new HashMap<>();
        serviceMap.put("cpu", new CpuService(cpuOverviewChart, cpuOverview, detailContainer));
        serviceMap.put("memory", new MemoryService(memoryOverviewChart, memoryOverview, detailContainer));
        serviceMap.put("device", new DeviceService(deviceOverviewChart, deviceOverview, detailContainer));

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
            case "cpu" -> cpuCard.getStyleClass().add("active");
            case "memory" -> memoryCard.getStyleClass().add("active");
            case "device" -> deviceCard.getStyleClass().add("active");
        }
        serviceMap.get(card).show();
        activeCardName = card;
    }
}
