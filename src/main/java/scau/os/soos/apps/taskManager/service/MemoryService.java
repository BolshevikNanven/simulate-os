package scau.os.soos.apps.taskManager.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import scau.os.soos.apps.taskManager.TaskManagerApp;
import scau.os.soos.apps.taskManager.TaskManagerService;

import java.io.IOException;

public class MemoryService implements TaskManagerService {
    private final ScrollPane detailContainer;
    private final VBox memoryDetail;

    public MemoryService(ScrollPane detailContainer) {
        this.detailContainer = detailContainer;
        try {
            memoryDetail = FXMLLoader.load(TaskManagerApp.class.getResource("memory/memory.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void show() {
        detailContainer.setContent(memoryDetail);
    }

    @Override
    public void render() {

    }
}
