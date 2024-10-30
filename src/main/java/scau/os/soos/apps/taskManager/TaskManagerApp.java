package scau.os.soos.apps.taskManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import scau.os.soos.common.OS;
import scau.os.soos.common.model.Handler;
import scau.os.soos.ui.components.base.Window;

import java.io.IOException;

public class TaskManagerApp extends Window {
    @FXML
    private ScrollPane detailArea;
    @FXML
    private Label clock;
    @FXML
    private VBox cardContainer;
    @FXML
    private HBox cpuCard;
    @FXML
    private HBox memoryCard;
    @FXML
    private HBox deviceCard;
    private VBox cpuDetail;
    private VBox memoryDetail;

    private VBox deviceDetail;

    private Handler handler;
    private String activeCardName;

    public TaskManagerApp() {
        super("任务管理器", "main.fxml", 920, 580);
    }

    @Override
    protected void initialize() {
        handler = () -> {
            Platform.runLater(() ->
                    clock.setText(String.valueOf(OS.clock.get()))
            );
        };

        OS.clock.bind(handler);

        loadDetail();
        addListener();

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
    }

    private void setActiveCard(String card) {
        for (Node child : cardContainer.getChildren()) {
            child.getStyleClass().remove("active");
        }
        switch (card) {
            case "cpu" -> {
                cpuCard.getStyleClass().add("active");
                detailArea.setContent(cpuDetail);
            }
            case "memory" -> {
                memoryCard.getStyleClass().add("active");
                detailArea.setContent(memoryDetail);
            }
            case "device" -> {
                deviceCard.getStyleClass().add("active");
                detailArea.setContent(deviceDetail);
            }
        }
        activeCardName = card;

    }

    private void loadDetail() {
        try {
            cpuDetail = FXMLLoader.load(this.getClass().getResource("cpu/cpu.fxml"));
            memoryDetail = FXMLLoader.load(this.getClass().getResource("memory/memory.fxml"));
            deviceDetail = FXMLLoader.load(this.getClass().getResource("device/device.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
