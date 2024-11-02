package scau.os.soos.apps.taskManager.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import scau.os.soos.apps.taskManager.TaskManagerApp;
import scau.os.soos.apps.taskManager.TaskManagerService;

import java.io.IOException;

public class CpuService implements TaskManagerService {
    private final ScrollPane detailContainer;
    private final VBox cpuDetail;

    public CpuService(ScrollPane detailContainer) {
        this.detailContainer = detailContainer;
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
}
